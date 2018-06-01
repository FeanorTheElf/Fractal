package simon.fractal.controller;

import java.beans.PropertyChangeListener;

import javax.swing.Action;

import simon.fractal.display.FractalComponent;

abstract class FractalComponentAction implements Action{
	
	private FractalComponent component;
	
	protected FractalComponent getComponent(){
		return component;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	@Override
	public void putValue(String key, Object value) {
	}

	@Override
	public void setEnabled(boolean b) {
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	}
	
	public FractalComponentAction(FractalComponent component){
		this.component = component;
	}
}
