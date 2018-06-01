package simon.fractal.formulas;

import simonUtil.complexMath.ComplexNumber;

public class FractalPart {

	protected ComplexNumber topLeft;
	protected ComplexNumber bottomRight;
	
	public ComplexNumber getTopLeft(){
		return topLeft;
	}
	
	public String toString(){
		return "[" + getTopLeft() + "] to [" + getBottomRight() + "]";
	}
	
	public static FractalPart parse(String val){
		String[] parts = val.split("\\] to \\[");
		if (parts.length != 2)
			throw new NumberFormatException();
		return new FractalPart(ComplexNumber.parseComplex(parts[0].substring(1)), ComplexNumber.parseComplex(parts[1].substring(0, parts[1].length() - 1)));
	}
	
	public double getWidth(){
		return Math.abs(bottomRight.subtract(topLeft).getReal());
	}
	
	public double getHeight(){
		return Math.abs(bottomRight.subtract(topLeft).getImaginary());
	}
	
	public ComplexNumber getBottomRight(){
		return bottomRight;
	}
	
	public ComplexNumber getCenter(){
		return topLeft.add(bottomRight).divide(ComplexNumber.TWO);
	}
	
	public FractalImageMap mapTo(int width, int height){
		return new FractalImageMap(this, 0, 0, width, height);
	}
	
	public FractalPart changeSideRelation(double widthToHeightRatio){
		double width = bottomRight.getReal() - topLeft.getReal();
		double height = topLeft.getImaginary() - bottomRight.getImaginary();
		if (width < height * widthToHeightRatio){ //this is "higher" than the target
			double scale = widthToHeightRatio * height /  width - 1;
			return new FractalPart(
					ComplexNumber.fromCartesian(this.topLeft.getReal() - (width * scale / 2), this.topLeft.getImaginary()),
					ComplexNumber.fromCartesian(this.bottomRight.getReal() + (width * scale / 2), this.bottomRight.getImaginary()));
		}else{
			double scale = width / widthToHeightRatio / height - 1;
			return new FractalPart(
					ComplexNumber.fromCartesian(this.topLeft.getReal(), this.topLeft.getImaginary() + (height * scale / 2)),
					ComplexNumber.fromCartesian(this.bottomRight.getReal(), this.bottomRight.getImaginary() - (height * scale / 2)));
		}
	}
	
	public FractalPart setCenter(ComplexNumber c){
		ComplexNumber difference = c.subtract(this.getCenter());
		return new FractalPart(this.topLeft.add(difference), this.bottomRight.add(difference));
	}
	
	public FractalImageMap mapTo(int x, int y, int width, int height){
		return new FractalImageMap(this, x, y, width, height);
	}
	
	public FractalPart normalize(double sideQuotient){
		return null;
	}
	
	public FractalPart zoom(double percentage){
		FractalPart result = new FractalPart(topLeft, bottomRight);
		ComplexNumber centerToTopLeft = result.topLeft.subtract(this.getCenter());
		result.topLeft = this.getCenter().add(centerToTopLeft.mult(ComplexNumber.valueOf(percentage)));
		result.bottomRight = this.getCenter().add(centerToTopLeft.mult(ComplexNumber.valueOf(-percentage)));
		this.getCenter().equals(result.getCenter());
		return result;
	}
	
	public FractalPart[] divide(int divisions){
		FractalPart[] result = new FractalPart[divisions];
		ComplexNumber divisionDelta = ComplexNumber.fromCartesian(getWidth() / divisions, 0);
		for (int i = 0; i < divisions; i++){
			result[i] = new FractalPart(topLeft.add(divisionDelta.mult(ComplexNumber.valueOf(i))), 
					bottomRight.subtract(divisionDelta.mult(ComplexNumber.valueOf(divisions - i - 1))));
		}
		return result;
	}
	
	public static FractalPart getContent(ComplexNumber first, ComplexNumber second){
		return new FractalPart(
				ComplexNumber.fromCartesian(Math.min(first.getReal(), second.getReal()), 
						Math.max(first.getImaginary(), second.getImaginary())), 
				ComplexNumber.fromCartesian(Math.max(first.getReal(), second.getReal()), 
						Math.min(first.getImaginary(), second.getImaginary())));
	}
	
	public FractalPart(ComplexNumber topLeft, ComplexNumber bottomRight){
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	public FractalPart(){
		this(ComplexNumber.fromCartesian(-2, 1), ComplexNumber.fromCartesian(1, -1));
	}
}
