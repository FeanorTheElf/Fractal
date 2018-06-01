package simon.fractal.formulas.implementations;

import java.util.Arrays;

import simon.fractal.formulas.FractalFormula;
import simonUtil.complexMath.ComplexNumber;

abstract class FractalClass implements FractalFormula{

	private ComplexNumber[] parameter;
	
	public boolean equals(Object that){
		if (that instanceof FractalClass){
			return ((FractalClass) that).getId().equals(this.getId()) && Arrays.deepEquals(((FractalClass) that).getParameters(), this.getParameters());
		}
		return false;
	}

	public ComplexNumber getParameter(int i){
		createParameterArray();
		return parameter[i];
	}
	
	public void setParameter(int i, ComplexNumber value){
		createParameterArray();
		this.parameter[i] = value;
	}
	
	private void createParameterArray(){
		if (parameter == null){
			this.parameter = new ComplexNumber[getParameterCount()];
			for (int i = 0; i < parameter.length; i++){
				this.parameter[i] = ComplexNumber.ZERO;
			}
		}
	}
	
	@Override
	public ComplexNumber[] getParameters() {
		return parameter;
	}
}
