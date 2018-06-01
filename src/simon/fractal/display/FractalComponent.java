package simon.fractal.display;

import javax.swing.JComponent;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.formulas.FractalPart;

public interface FractalComponent {

	JComponent getComponent();
	FractalImageMap getImageMap();
	
	void setFormula(FractalFormula formula);
	void setAccurancy(int accurancy);
	void setDisplayedPart(FractalPart part);
	void setColoringPattern(ColoringPattern pattern);
	
	FractalFormula getFormula();
	int getAccurancy();
	ColoringPattern getColoringPattern();
}
