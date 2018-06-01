package simon.fractal.formulas.implementations;

import simon.fractal.formulas.FractalBuildException;
import simonUtil.complexMath.ComplexNumber;

public class Mandelbrot4Set extends FractalClass{

	@Override
	public String getName() {
		return "Simonmenge";
	}

	@Override
	public String getId() {
		return "simon";
	}

	@Override
	public int getParameterCount() {
		return 0;
	}

	@Override
	public String[] getFormula() throws FractalBuildException {
		return new String[]{
				"double z_real = 0.; double z_im = 0.; double new_real;", 
				"new_real = z_real * z_real * (z_real * z_real - 6 * z_im * z_im) + z_im * z_im * z_im * z_im + c_real; " +
				"z_im = 4 * z_real * z_im * (z_real * z_real - z_im * z_im) + c_im; " +
				"z_real = new_real;"};
	}

	@Override
	public int getIterationCount(ComplexNumber point, int maxIteraions) {
		double z_real = 0;
		double z_im = 0;
		double newReal = 0;
		for (int i = 0; i <= maxIteraions; i++) {
			newReal = z_real * z_real * z_real - 3 * z_real * z_im * z_im + point.getReal();
			z_im = 3 * z_im * z_real * z_real - z_im * z_im * z_im + point.getImaginary();
			z_real = newReal;
			if (z_real * z_real + z_im * z_im >= 4.0) {
				return i;
			}
		}
		return -1;
	}

}
