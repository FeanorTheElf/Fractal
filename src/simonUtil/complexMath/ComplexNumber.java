package simonUtil.complexMath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ComplexNumber {

	public static final ComplexNumber ZERO = new ComplexNumber(0, 0);
	public static final ComplexNumber ONE = new ComplexNumber(1, 0);
	public static final ComplexNumber I = new ComplexNumber(0, 1);
	public static final ComplexNumber PI = new ComplexNumber(Math.PI, 0);
	public static final ComplexNumber TWO = new ComplexNumber(2, 0);
	
	private static final Pattern parsePattern = Pattern.compile("(?:(?<real>(?:\\+|-)?[^-\\+i]+)?(?:(?<img>(?:\\+|-).*)i)?)|(?:(?<img2>.*)i)");
	
	private double real;
	private double imaginary;
	
	public String toString(){
		if (imaginary == 0){
			return real + "";
		}else if (real == 0){
			return imaginary + "i";
		}else if (imaginary == 1){
			return real + "+i";
		}else if (imaginary == -1){
			return real + "-i";
		}
		return real + (imaginary >= 0 ? "+" : "") + imaginary + "i";
	}
	
	public static ComplexNumber parseComplex(final String value){
		Matcher m = parsePattern.matcher(value.replaceAll(" ", ""));
		if (!m.matches())
			throw new NumberFormatException();
		double real = 0;
		String img = null;
		if (m.group("real") != null)
			real = Double.parseDouble(m.group("real"));
		if (m.group("img") != null)
			img = m.group("img");
		if (m.group("img2") != null)
			img = m.group("img2");
		if (img != null && (img.equals("") || img.equals("+") || img.equals("-")))
			img += "1";
		return ComplexNumber.fromCartesian(real, img != null ? Double.parseDouble(img) : 0);
	}
	
	public boolean equals(Object that){
		if (that instanceof ComplexNumber){
			return ((ComplexNumber)that).real == this.real && ((ComplexNumber)that).imaginary == this.imaginary;
		}
		return false;
	}
	
	public double getReal(){
		return real;
	}
	
	public double getImaginary(){
		return imaginary;
	}
	
	public double getR(){
		return absoluteValue();
	}
	
	public double getPhi(){
		return Math.atan2(imaginary, real);
	}
	
	public double absoluteValue(){
		return Math.sqrt(real * real + imaginary * imaginary);
	}
	
	public ComplexNumber mult(ComplexNumber that){
		return new ComplexNumber(this.real * that.real - this.imaginary * that.imaginary, 
				this.imaginary * that.real + this.real * that.imaginary);
	}
	
	public ComplexNumber add(ComplexNumber that){
		return new ComplexNumber(this.real + that.real, this.imaginary + that.imaginary);
	}
	
	public ComplexNumber divide(ComplexNumber that){
		return new ComplexNumber(
				(this.real * that.real + this.imaginary * that.imaginary) / 
				(that.real * that.real + that.imaginary * that.imaginary), 
				(this.imaginary * that.real - this.real * that.imaginary) / 
				(that.real * that.real + that.imaginary * that.imaginary));
	}
	
	public ComplexNumber subtract(ComplexNumber that){
		return new ComplexNumber(this.real - that.real, this.imaginary - that.imaginary);
	}
	
	private ComplexNumber(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	
	public static ComplexNumber fromCartesian(double real, double imaginary){
		return new ComplexNumber(real, imaginary);
	}
	
	public static ComplexNumber fromPolar(double r, double theta){
		ComplexNumber result = new ComplexNumber(0, 0);
		result.imaginary = Math.sin(theta) * r;
		result.real = Math.cos(theta) * r;
		return result;
	}
	
	public static ComplexNumber valueOf(double value){
		return new ComplexNumber(value, 0);
	}
}
