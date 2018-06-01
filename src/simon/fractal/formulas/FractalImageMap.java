package simon.fractal.formulas;

import java.util.Iterator;

import simonUtil.complexMath.ComplexNumber;
import simonUtil.util.IntTupel;

public class FractalImageMap implements Iterable<IntTupel>{

	private FractalPart part;
	private int width;
	private int height;
	private int x;
	private int y;
	private double complexUnitXPerPixel;
	private double complexUnitYPerPixel;
	
	public String toString(){
		return part.toString();
	}
	
	public IntTupel getPoint(ComplexNumber number){
		return new IntTupel((int)(number.subtract(part.getTopLeft()).getReal() / complexUnitXPerPixel + x), (int)(number.subtract(part.getTopLeft()).getImaginary() / complexUnitYPerPixel + y));
	}
	
	public double getScale(){
		if (complexUnitXPerPixel == complexUnitYPerPixel){
			return complexUnitXPerPixel;
		}else{
			return Math.min(complexUnitXPerPixel, complexUnitYPerPixel);
		}
	}
	
	public FractalPart getFractalPart(){
		return part;
	}
	
	public double getComplexUnitsXPerPixel(){
		return complexUnitXPerPixel;
	}
	
	public double getComplexUnitsYPerPixel(){
		return complexUnitYPerPixel;
	}
	
	public int getStartX(){
		return x;
	}
	
	public int getStartY(){
		return y;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getPixelCount(){
		return getWidth() * getHeight();
	}
	
	public ComplexNumber getComplexNumber(int x, int y){
		return ComplexNumber.fromCartesian((x - this.x) * complexUnitXPerPixel, -(y - this.y) * complexUnitYPerPixel).add(part.getTopLeft());
	}
	
	public FractalImageMap[] divide(int divisions){
		int untilNow = 0;
		double partwidth = (double)getWidth() / (double)divisions;
		FractalImageMap[] result = new FractalImageMap[divisions];
		FractalPart[] parts = part.divide(divisions);
		for (int i = 0; i < divisions; i++){
			int width = (int)((i + 1) * partwidth) - untilNow;
			result[i] = new FractalImageMap(parts[i], complexUnitXPerPixel, complexUnitYPerPixel, (int)untilNow, 0, width, height);
			untilNow += width;
		}
		return result;
	}
	
	public FractalImageMap(FractalPart part, int x, int y, int width, int height){
		this.part = part;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.complexUnitXPerPixel = part.getWidth() / width;
		this.complexUnitYPerPixel = part.getHeight() / height;
	}
	
	private FractalImageMap(FractalPart part, double complexUnitXPerPixel, double complexUnitYPerPixel, int x, int y, int width, int height){
		if (width < 0 || height < 0){
			throw new IllegalArgumentException("Width and height must not be negative!");
		}
		this.part = part;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.complexUnitXPerPixel = complexUnitXPerPixel;
		this.complexUnitYPerPixel = complexUnitYPerPixel;
	}

	@Override
	public Iterator<IntTupel> iterator() {
		return new Iterator<IntTupel>() {
			
			int currentX = -1;
			int currentY = 0;
			
			@Override
			public boolean hasNext() {
				return (currentY + 1) < height || (currentY < height && (currentX + 1) < width);
			}

			@Override
			public IntTupel next() {
				currentX++;
				if (currentX >= width){
					currentX = 0;
					currentY++;
				}
				return new IntTupel(currentX + x, currentY + y);
			}
		};
	}
}
