package simon.fractal.rendering.gpu;

import org.lwjgl.opencl.CLCommandQueue;

import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.RenderMission;

public interface FractalRenderProgram extends AutoCloseable{

	void execute(RenderMission m, CLCommandQueue queue, int accurancy) throws CLException, FractalRendererException;
}
