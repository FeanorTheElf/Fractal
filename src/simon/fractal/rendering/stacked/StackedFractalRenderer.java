package simon.fractal.rendering.stacked;

import java.util.Stack;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;

public class StackedFractalRenderer implements FractalRenderer{
	
	private static final Logger logger = LoggerFactory.create(StackedFractalRenderer.class);
	
	private Stack<FractalRendererFactory> factories = new Stack<>();
	private FractalRenderer current;
	
	private FractalFormula fractal;
	private ColoringPattern pattern;
	private int accurancy;
	
	private void showInitError(String oldName, String newName, Exception ex){
		logger.error("Error while initializing: " + oldName + "\r\n"
				+ "Instead using: " + newName, ex);
	}
	
	private void showRenderError(String oldName, String newName, Exception ex){
		logger.error("Error while rendering: " + oldName + "\r\n"
				+ "Instead using: " + newName, ex);
	}
	
	private void closeCurrent(){
		try {
			current.close();
		} catch (Exception e) {
			logger.error("Error while closing FractalRenderer: " + current, e);
		}
	}
	
	private String getNextRendererName(){
		try {
			return this.factories.isEmpty() ? "[none]" : this.factories.peek().getClass().getMethod("build").getReturnType().getName();
		} catch (NoSuchMethodException | SecurityException e) {
			logger.fatal(e);
			System.exit(0);
			return null;
		}
	}
	
	private void buildTop(){
		if (factories.isEmpty()){
			logger.fatal("No correct default Renderer for StackedFractalRenderer defined");
			System.exit(0);
		}
		FractalRendererFactory factory = factories.pop();
		try{
			current = factory.build();
			current.setFractalFormula(this.fractal);
			current.setAccurancy(this.accurancy);
			current.setColoringPattern(this.pattern);
		}catch (FractalBuildException e) {
			try {
				showInitError(factory.getClass().getMethod("build").getReturnType().getName(), getNextRendererName(), e);
			} catch (NoSuchMethodException | SecurityException e1) {
				logger.fatal(e1);
				System.exit(0);
			}
			buildTop();
		}
	}

	@Override
	public void close() throws Exception {
		current.close();
	}

	@Override
	public void render(FractalImageMap part, FractalRenderingHandle callback) {
		try{
			current.render(part, callback);
		}catch (FractalRendererException e) {
			closeCurrent();
			showRenderError(current.getClass().getName(), getNextRendererName(), e);
			buildTop();
			render(part, callback);
		}
	}

	@Override
	public void cancel() {
		current.cancel();
	}

	@Override
	public void waitForFinished() throws FractalRendererException {
		try{
			current.waitForFinished();
		}catch(FractalRendererException ex){
			closeCurrent();
			showRenderError(current.getClass().getName(), getNextRendererName(), ex);
			buildTop();
		}
	}

	@Override
	public void setFractalFormula(FractalFormula f) {
		this.fractal = f;
		this.current.setFractalFormula(f);;
	}

	@Override
	public void setAccurancy(int accurancy) {
		this.current.setAccurancy(accurancy);
		this.accurancy = accurancy;
	}

	@Override
	public void setColoringPattern(ColoringPattern pattern) {
		this.pattern = pattern;
		this.current.setColoringPattern(pattern);
	}

	public StackedFractalRenderer(Stack<FractalRendererFactory> factoryStack){
		this.factories = factoryStack;
		this.buildTop();
	}
}
