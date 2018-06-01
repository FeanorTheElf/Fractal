package simon.fractal.controller;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

import simon.fractal.display.FractalComponent;
import simon.fractal.formulas.FractalPart;

class PasteFractalPartFromClipboard extends FractalComponentAction{

	public PasteFractalPartFromClipboard(FractalComponent component) {
		super(component);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String clipboardContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			FractalPart newPart = FractalPart.parse(clipboardContent);
			this.getComponent().setDisplayedPart(newPart);
		} catch (HeadlessException | UnsupportedFlavorException | IOException | NumberFormatException ex) {
			System.err.println("[Controller] Problem with accessing clipboard content, skipping command");
		}
	}

}
