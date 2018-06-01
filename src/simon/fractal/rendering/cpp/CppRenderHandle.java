package simon.fractal.rendering.cpp;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.formulas.FractalFormula;

class CppRenderHandle {
	
	private long ptr;
	
	private static int[] createColorParams(ColoringPattern coloring) {
		int[] paramsColor = new int[coloring.getParameterCount()];
		for (int i = 0; i < paramsColor.length; ++i){
			paramsColor[i] = coloring.getParameter(i);
		}
		return paramsColor;
	}

	private static void fillRenderParams(double[] paramsReal, double[] paramsIm, FractalFormula formula) {
		for (int i = 0; i < paramsReal.length; ++i){
			paramsReal[i] = formula.getParameter(i).getReal();
			paramsIm[i] = formula.getParameter(i).getImaginary();
		}
	}
	
	private static native long create(int threadCount);

	private static native void renderNative(
			long ptr,
			int[] output,
			String fractalId,
			String colorId,
			int maxIterations,
			double complexUnitXPerPixel,
			double complexUnitYPerPixel,
			double top_left_real,
			double top_left_imaginary,
			int width,
			int height,
			double[] parameterReal,
			double[] parameterIm,
			int[] parameterColor);
	
	private static native void cancelNative(long ptr);
	
	private static native void closeNative(long ptr);
	
	void render(
			int[] output,
			FractalFormula f,
			ColoringPattern p,
			int maxIterations,
			double complexUnitXPerPixel,
			double complexUnitYPerPixel,
			double top_left_real,
			double top_left_imaginary,
			int width,
			int height){
		double[] paramsReal = new double[f.getParameterCount()];
		double[] paramsIm = new double[f.getParameterCount()];
		fillRenderParams(paramsReal, paramsIm, f);
		renderNative(ptr, output, f.getId(), p.getId(), maxIterations, 
				complexUnitXPerPixel, complexUnitYPerPixel, 
				top_left_real, top_left_imaginary, width, height, 
				paramsReal, paramsIm, createColorParams(p));
	}
	
	void cancel(){
		cancelNative(ptr);
	}
	
	void close(){
		closeNative(ptr);
	}
	
	CppRenderHandle(int threadCount) throws FractalBuildException{
		try{
			System.loadLibrary("FractalJNI");
		}catch (UnsatisfiedLinkError ex){
			throw new FractalBuildException(ex);
		}
		this.ptr = create(threadCount);
	}
}
