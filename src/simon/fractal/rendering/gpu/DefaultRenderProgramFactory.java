package simon.fractal.rendering.gpu;

import simon.fractal.formulas.FractalBuildException;

public class DefaultRenderProgramFactory implements RenderProgramFactory{

	@Override
	public FractalRenderProgram create() throws CLException, FractalBuildException {
		return new SimpleFractalRenderProgram();
	}
}
