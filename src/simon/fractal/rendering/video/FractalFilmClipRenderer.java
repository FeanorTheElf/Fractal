package simon.fractal.rendering.video;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalPart;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;

public class FractalFilmClipRenderer {
	
	private static final Logger logger = LoggerFactory.create(FractalFilmClipRenderer.class);

	private float fps = 40;
	private volatile boolean cancelled = false;
	private KeyframeSet fractalParts;
	private FractalRenderer renderer;
	private int width, height;
	private int i = 0;
	
	public void createImageFiles(String name) throws UnsupportedEncodingException, IOException{
		File f = new File(name);
		f.mkdir();
		i = 0;
		cancelled = false;
		int transitionNumber = 0;
		
		if (fractalParts.getKeyframeNumber() < 2){
			logger.error("At least 2 FractalParts are necessary to interpolate a image sequence");
			return;
		}
		Iterator<FractalPart> it = fractalParts.iterator();
		FractalPart last = null;
		FractalPart current = it.next();
		double sideRatio = (double)width / (double)height;
		while (it.hasNext()){
			last = current;
			current = it.next();
			interpolate(f, last, current, sideRatio, transitionNumber);
			if (cancelled){
				return;
			}
			++transitionNumber;
		}
	}

	private void interpolate(File f, FractalPart last, FractalPart current, double sideRatio, int transitionNumber) throws UnsupportedEncodingException, IOException{
		FractalPartInterpolator fpi = new FractalPartInterpolator(last.changeSideRelation(sideRatio), current.changeSideRelation(sideRatio));
		double totalTime = fpi.getTimeNeeded();
		for (double t = 0; t <= totalTime; t += (1 / fps)){
			try {
				render(fpi, t, new File(f.getAbsolutePath() + String.format("\\image%06d.png", i++)));
			} catch (FractalRendererException e) {
				logger.error(e);
				cancel();
			}
			if (cancelled){
				return;
			}
			logger.info("rendered image t=" + t + " / " + totalTime + "; transition " + transitionNumber + " / " + (fractalParts.getKeyframeNumber() - 1));
		}
	}
	
	private void render(FractalPartInterpolator fpi, double t, File save) throws FractalRendererException{
		double alpha = fpi.calcAlpha(t);
		FractalPart part = fpi.getInterpolatedFractalPart(fpi.calcA(alpha));
		renderer.render(part.mapTo(width, height), new FractalRenderingHandle() {
			
			@Override
			public void statusChange(double currentPercentage) {}
			
			@Override
			public void calculationFinished(Image image) {
				try{
					if (image instanceof RenderedImage){
						ImageIO.write((RenderedImage) image, "png", save);
					}else{
						BufferedImage copy = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
						copy.getGraphics().drawImage(image, 0, 0, null);
						ImageIO.write(copy, "png", save);
					}
				}catch (IOException e) {
					logger.error(e);
				}
			}
		});
		renderer.waitForFinished();
	}
	
	public void cancel(){
		this.cancelled = true;
		this.renderer.cancel();
	}
	
	public void setFPS(float fps){
		this.fps = fps;
	}
	
	public void setFractal(FractalFormula f){
		this.renderer.setFractalFormula(f);
	}
	
	public void setColoring(ColoringPattern p){
		this.renderer.setColoringPattern(p);;
	}
	
	public void setAccurancy(int accurancy){
		this.renderer.setAccurancy(accurancy);;
	}
	
	public void setResultDimension(int width, int height){
		this.width = width;
		this.height = height;
	}

	public FractalFilmClipRenderer(FractalRenderer r, KeyframeSet keyframes){
		this.fractalParts = keyframes;
		this.renderer = r;
	}
}
