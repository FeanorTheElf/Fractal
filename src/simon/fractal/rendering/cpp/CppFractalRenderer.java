package simon.fractal.rendering.cpp;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;
import simon.fractal.rendering.RenderMission;

@Deprecated
public class CppFractalRenderer implements FractalRenderer{
	
	private BufferedImage result;
	private long startTime;
	private CppRenderHandle handle;
	
	private ExecutorService service;
	private CppRenderTask task;
	private Future<?> future;
	
	private class CppRenderTask implements Callable<Void>{
		
		private RenderMission mission;
		private ColoringPattern coloring;
		private FractalFormula formula;
		private int accurancy;

		@Override
		public Void call() throws Exception {
			startTime = System.currentTimeMillis();
			int[] data = ((DataBufferInt)((WritableRaster)result.getRaster()).getDataBuffer()).getData();
			int maxIterations = -(int)(Math.log10(mission.getPart().getScale()) * this.accurancy);
			handle.render(data, 
					formula,
					coloring,
					maxIterations, 
					mission.getPart().getComplexUnitsXPerPixel(), 
					mission.getPart().getComplexUnitsYPerPixel(), 
					mission.getPart().getFractalPart().getTopLeft().getReal(), 
					mission.getPart().getFractalPart().getTopLeft().getImaginary(), 
					mission.getPart().getWidth(), 
					mission.getPart().getHeight());
			log("Rendered in " + (System.currentTimeMillis() - startTime));
			mission.getCallback().calculationFinished(result);
			return null;
		}
		
	}
	
	private void log(String msg){
		System.out.println("[Renderer] " + msg);
	}

	@Override
	public void close() throws Exception {
		handle.close();
	}

	@Override
	public void render(FractalImageMap part, FractalRenderingHandle callback) throws FractalRendererException {
		waitForFinished();
		if (result == null || result.getWidth() != part.getWidth() || result.getHeight() != part.getHeight()){
			log("Creating new image");
			result = new BufferedImage(part.getWidth(), part.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		this.task.mission = new RenderMission(part, callback, null, null);
		future = service.submit(task);
	}

	@Override
	public void cancel() {
		handle.cancel();
	}

	@Override
	public void waitForFinished() throws FractalRendererException {
		try {
			if (this.future != null)
				this.future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFractalFormula(FractalFormula f) {
		this.task.formula = f;
	}

	@Override
	public void setAccurancy(int accurancy) {
		this.task.accurancy = accurancy;
	}

	@Override
	public void setColoringPattern(ColoringPattern pattern) {
		this.task.coloring = pattern;
	}
	
	public CppFractalRenderer() throws FractalBuildException{
		service = Executors.newFixedThreadPool(1);
		task = this.new CppRenderTask();
		handle = new CppRenderHandle(Runtime.getRuntime().availableProcessors());
	}
}
