package simon.fractal.controller;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import simon.fractal.display.FractalComponent;

class CopyFractalPartToClipboard extends FractalComponentAction{

	public CopyFractalPartToClipboard(FractalComponent component) {
		super(component);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.getComponent().getImageMap().getFractalPart().toString()), null);
	}
}
