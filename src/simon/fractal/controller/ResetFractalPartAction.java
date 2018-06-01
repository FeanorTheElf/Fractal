package simon.fractal.controller;

import java.awt.event.ActionEvent;

import simon.fractal.display.FractalComponent;
import simon.fractal.formulas.FractalPart;

class ResetFractalPartAction extends FractalComponentAction{

	public ResetFractalPartAction(FractalComponent component) {
		super(component);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getComponent().setDisplayedPart(new FractalPart());
	}
}
