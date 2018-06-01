package simon.fractal.coloring;

public interface ColoringPattern {

	int getColor(int iterations);
	String getId();
	public int getParameterCount();
	public void setParameter(int index, int value);
	public int getParameter(int index);
	public int[] getParameters();
	/**
	 * returns the formula calculating the argb int value for a given
	 * convergence behavior. The result array should contain the following
	 * values
	 * [0]: color if the sequence converges
	 * [1]: color if the sequence diverges. This formula should use i to
	 * 		access the number of iterations done.
	 * @return
	 */
	String[] getFormula();
}
