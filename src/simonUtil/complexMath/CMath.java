package simonUtil.complexMath;

public class CMath {

	public static ComplexNumber pow(ComplexNumber base, ComplexNumber exponent){
		return ComplexNumber.fromPolar(Math.exp(-exponent.getImaginary() * base.getPhi()) * Math.pow(base.getR(), exponent.getReal()), 
				exponent.getReal() * base.getPhi() + Math.log(base.getR()) * exponent.getImaginary());
	}
	
	public static ComplexNumber sin(ComplexNumber that){
		return ComplexNumber.fromCartesian(Math.sin(that.getReal()) * Math.cosh(that.getImaginary()), 
				Math.cos(that.getReal()) * Math.sinh(that.getImaginary()));
	}
	
	public static ComplexNumber cos(ComplexNumber that){
		return ComplexNumber.fromCartesian(Math.cos(that.getReal()) * Math.cosh(that.getImaginary()), 
				-Math.sin(that.getReal()) * Math.sinh(that.getImaginary()));
	}
}
