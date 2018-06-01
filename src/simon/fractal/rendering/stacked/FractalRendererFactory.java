package simon.fractal.rendering.stacked;

import simon.fractal.formulas.FractalBuildException;
import simon.fractal.rendering.FractalRenderer;

public interface FractalRendererFactory {

	FractalRenderer build() throws FractalBuildException;
}
