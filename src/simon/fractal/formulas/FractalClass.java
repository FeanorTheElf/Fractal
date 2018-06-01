package simon.fractal.formulas;

import simonUtil.complexMath.ComplexNumber;

public abstract class FractalClass implements FractalFormula{

	private ComplexNumber[] parameter;

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
