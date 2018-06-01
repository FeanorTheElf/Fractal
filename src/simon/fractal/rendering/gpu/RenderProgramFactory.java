package simon.fractal.rendering.gpu;

import simon.fractal.formulas.FractalBuildException;

public interface RenderProgramFactory {

	FractalRenderProgram create() throws CLException, FractalBuildException;
}
