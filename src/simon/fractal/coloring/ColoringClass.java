package simon.fractal.coloring;

import java.util.Arrays;

public abstract class ColoringClass implements ColoringPattern{

	private int[] parameter;
	
	public boolean equals(Object that){
		if (that instanceof ColoringClass){
			return ((ColoringClass) that).getId().equals(this.getId()) && Arrays.equals(((ColoringClass) that).getParameters(), this.getParameters());
		}
		return false;
	}

	public int getParameter(int i){
		createParameterArray();
		return parameter[i];
	}
	
	public void setParameter(int i, int value){
		createParameterArray();
		this.parameter[i] = value;
	}
	
	private void createParameterArray(){
		if (parameter == null){
			this.parameter = new int[getParameterCount()];
			for (int i = 0; i < parameter.length; i++){
				this.parameter[i] = 0;
			}
		}
	}
	
	@Override
	public int[] getParameters() {
		return parameter;
	}
}
