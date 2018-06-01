package simon.fractal.rendering.video;

import simon.fractal.formulas.FractalPart;
import simonUtil.complexMath.ComplexNumber;

class FractalPartInterpolator {

	private FractalPart start, end;
	private double speed = 1;
	
	public double startToEndRatio(){
		return 	start.getTopLeft().subtract(start.getBottomRight()).getReal() / 
				end.getTopLeft().subtract(end.getBottomRight()).getReal();
	}
	
	public double endToStartRatio(){
		return 1 / startToEndRatio();
	}
	
	public double sizeRatio(){
		return startToEndRatio() > 1 ? startToEndRatio() : 1 / startToEndRatio();
	}
	
	public double getTimeNeeded(){
		return Math.log(sizeRatio()) / speed;
	}
	
	/**
	 * Returns the point the vertices go to or away when the corresponding rectangular fractal part is interpolated;
	 * this is the intersection of the line [start.getTopLeft(); end.getTopLeft()] and of the line 
	 * [start.getBottomRight(); end.getBottomRight()]
	 * @return
	 */
	public ComplexNumber getVanishingPoint(){
		/**
		 * the top left start point be A, the top left end point be B and the vanishing point be C
		 * this ratio is |AC| / |AB|, as can be calculated with the intercept theorem
		 */
		double ratio = startToEndRatio() / (startToEndRatio() - 1);
		return end.getTopLeft().subtract(start.getTopLeft()).mult(ComplexNumber.valueOf(ratio)).add(start.getTopLeft());
	}
	
	/**
	 * Calculates the fractal part between start and end whose diagonal is A times the start diagonal;
	 * to make this to work, a must be between 1 and destinationRatio()
	 * @param a
	 * @return
	 */
	public FractalPart getInterpolatedFractalPart(double a){
		ComplexNumber vanishingPoint = getVanishingPoint();
		ComplexNumber relativeTopLeft = this.start.getTopLeft().subtract(vanishingPoint);
		ComplexNumber relativeBottomRight = this.start.getBottomRight().subtract(vanishingPoint);
		return new FractalPart(vanishingPoint.add(relativeTopLeft.mult(ComplexNumber.valueOf(a))), 
				vanishingPoint.add(relativeBottomRight.mult(ComplexNumber.valueOf(a))));
	}
	
	/**
	 * calculates the interpolation value a from alpha; a linear change of a would result in a 
	 * perceived increase of interpolation speed; so this method calculates a from alpha
	 * in a way so that a linear change of alpha results in a constant interpolation speed
	 * @param alpha in range [0;1]
	 * @return
	 */
	public double calcA(double alpha){
		return Math.pow(endToStartRatio(), alpha);
	}
	
	/**
	 * calculates the percentage of the end frame from the total; is equal to
	 * 
	 *   t              tmax
	 *  /\               /\
	 *  \                \
	 *   \  speed(x) dx : \  speed(x) dx
	 *    \                \
	 *   \/               \/
	 *   0                0
	 * 
	 * the result will be in range [0;1]
	 * @param t
	 * @return
	 */
	public double calcAlpha(double t){
		return t / getTimeNeeded();
	}
	
	public FractalPartInterpolator(FractalPart start, FractalPart end){
		this.start = start;
		this.end = end;
	}
}
