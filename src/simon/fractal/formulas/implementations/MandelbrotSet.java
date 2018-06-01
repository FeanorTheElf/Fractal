package simon.fractal.formulas.implementations;

import simonUtil.complexMath.ComplexNumber;

public final class MandelbrotSet extends FractalClass{
	
	@Override
	public int getParameterCount() {
		return 0;
	}

	@Override
	public String getName() {
		return "Mandelbrotmenge";
	}

	@Override
	public int getIterationCount(ComplexNumber point, int maxIteraions) {
		double currentReal = 0;
		double real_square;
		double im_square;
		double currentImaginary = 0;
		for (int i = 0; i <= maxIteraions; i++) {
			real_square = currentReal * currentReal;
			im_square = currentImaginary * currentImaginary;
			currentImaginary = currentReal * currentImaginary * 2 + point.getImaginary();
			currentReal = real_square - im_square + point.getReal();
			if (real_square + im_square >= 4.0) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean equals(Object that){
		if (that instanceof MandelbrotSet){
			return true;
		}
		return false;
	}

	@Override
	public String[] getFormula() {
		return new String[]{
				"double z_real = 0.; " +
				"double z_im = 0.; " +
				"double real_square; " +
				"double im_square;", 
			
				"real_square = z_real * z_real;" +
				"im_square = z_im * z_im;" + 
				"z_im = 2 * z_real * z_im + c_im;" +
				"z_real = real_square - im_square + c_real;"};
	}

	@Override
	public String getId() {
		return "mandelbrot";
	}
}
