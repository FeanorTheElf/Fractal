package simon.fractal.formulas;

import simonUtil.complexMath.ComplexNumber;

public interface FractalFormula {

	public String getName();
	public String getId();
	public int getParameterCount();
	public void setParameter(int index, ComplexNumber value);
	public ComplexNumber getParameter(int index);
	public ComplexNumber[] getParameters();
	
	/**
	 * returns the formula calculating this fractal. The result is
	 * a string array containing the following data:
	 * [0]: init
	 * [1]: step
	 * The formulas may use c_real, c_im to access the real/imaginary
	 * part of the current pixel. At the end of step, the result
	 * value should be saved int z_real, z_im.
	 * @return
	 */
	public String[] getFormula() throws FractalBuildException;
	public int getIterationCount(ComplexNumber point, int maxIteraions);
}
