package simon.fractal.formulas.implementations;

import simonUtil.complexMath.ComplexNumber;

public final class MandelbrotSetFixedDecimal extends FractalClass{
	
	private static final long decimals = 48;
	private static final long second = 0xFFFFFFFF;
	
	@Override
	public int getParameterCount() {
		return 0;
	}

	@Override
	public String getName() {
		return "Mandelbrotmenge";
	}
	
	//does not work with signed numbers :(
	private static final long fixed_mult(final long a, final long b){
		long result = (a >> 32) * (b &second);
		result += (a & second) * (b >> 32);
		result >>= decimals - 32;
		result += ((a >> 32) * (b >> 32)) << (64 - decimals);
		result += ((a &second) * (b &second)) >> decimals;
		return result;
	}

	@Override
	public int getIterationCount(ComplexNumber point, int maxIteraions) {
		long currentReal = 0;
		long currentImaginary = 0;
		long newReal;
		final long cReal = (long)(point.getReal() * (1L << decimals));
		final long cIm = (long)(point.getImaginary() * (1L << decimals));
		
//		double dReal = 0;
//		double dIm = 0;
		for (int i = 0; i <= maxIteraions; i++) {
//			double newDReal = dReal * dReal - dIm * dIm + point.getReal();
//			dIm = 2 * dReal * dIm + point.getImaginary();
//			dReal = newDReal;
			
			newReal = fixed_mult(currentReal, currentReal) - fixed_mult(currentImaginary, currentImaginary) + cReal;
			currentImaginary = 2 * fixed_mult(currentImaginary, currentReal) + cIm;
			currentReal = newReal;
			if (fixed_mult(currentImaginary, currentImaginary) + fixed_mult(currentReal, currentReal) >= (4L << decimals)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean equals(Object that){
		if (that instanceof MandelbrotSetFixedDecimal){
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
