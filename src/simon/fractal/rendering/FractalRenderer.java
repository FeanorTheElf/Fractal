package simon.fractal.rendering;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;

public interface FractalRenderer extends AutoCloseable{

	void render(FractalImageMap part, FractalRenderingHandle callback) throws FractalRendererException;
	void cancel();
	void waitForFinished() throws FractalRendererException;
	void setFractalFormula(FractalFormula f);
	void setAccurancy(int accurancy);
	void setColoringPattern(ColoringPattern pattern);

}
