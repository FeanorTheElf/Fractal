package simon.fractal.controller;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

import simon.fractal.ExitController;
import simon.fractal.display.FractalComponent;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;
import simon.fractal.rendering.gpu.CLException;
import simon.fractal.rendering.gpu.DefaultRenderProgramFactory;
import simon.fractal.rendering.stacked.FractalRendererFactory;
import simon.fractal.rendering.stacked.StackedFractalRenderer;

class RenderFractalImageAction extends FractalComponentAction{
	
	private static final Logger logger = LoggerFactory.create(RenderFractalImageAction.class);
	private FractalRenderer renderer;
	
	public RenderFractalImageAction(FractalComponent component) {
		super(component);
		final Stack<FractalRendererFactory> factories = new Stack<>();
		factories.push(simon.fractal.rendering.cpu.ParallelFractalRenderer::new);
		factories.push(() -> {
			try {
				return new simon.fractal.rendering.gpu.GPUFractalRenderer(new DefaultRenderProgramFactory());
			} catch (CLException ex) {
				throw new FractalBuildException(ex);
			}
		});
		renderer = new StackedFractalRenderer(factories);
		ExitController.getInstance().closeAtExit(renderer);
	}
	
	private static File getFile(){
		File result = new File("output0.png");
		for (int i = 1; result.exists(); ++i){
			result = new File("output" + i + ".png");
		}
		return result;
	}
	
	private static void saveImage(Image image){
		RenderedImage result;
		if (image instanceof RenderedImage)
			result = (BufferedImage)image;
		else{
			result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			((BufferedImage)result).getGraphics().drawImage(image, 0, 0, null);
		}
		try {
			File f = getFile();
			ImageIO.write(result, "png", f);
			logger.info("Rendered image to " + f.getCanonicalPath());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent event) {
		try {
			renderer.setAccurancy(getComponent().getAccurancy());
			renderer.setFractalFormula(getComponent().getFormula());
			renderer.setColoringPattern(getComponent().getColoringPattern());
			renderer.render(getComponent().getImageMap(),
					FractalRenderingHandle.makeHandle(RenderFractalImageAction::saveImage));
			renderer.waitForFinished();
		} catch (FractalRendererException e) {
			logger.error(e);
		}
	}

}
