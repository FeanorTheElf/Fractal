
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Stack;

import javax.swing.JFrame;

import simon.fractal.ExitController;
import simon.fractal.coloring.DefaultColoring;
import simon.fractal.controller.Controller;
import simon.fractal.display.FractalComponent;
import simon.fractal.display.SimpleRenderedPanel;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.formulas.FractalPart;
import simon.fractal.formulas.implementations.MandelbrotSet;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.cpp.CppFractalRenderer;
import simon.fractal.rendering.gpu.CLException;
import simon.fractal.rendering.gpu.DefaultRenderProgramFactory;
import simon.fractal.rendering.gpu.GPUEnvironment;
import simon.fractal.rendering.gpu.GPUFractalRenderer;
import simon.fractal.rendering.stacked.FractalRendererFactory;
import simon.fractal.rendering.stacked.StackedFractalRenderer;

public class Main {

	public static void main(String[] args) throws Exception{
		final Stack<FractalRendererFactory> factories = new Stack<>();
		factories.push(simon.fractal.rendering.cpu.ParallelFractalRenderer::new);
		factories.push(() -> {
			try {
				return new simon.fractal.rendering.gpu.GPUFractalRenderer(new DefaultRenderProgramFactory());
			} catch (CLException ex) {
				throw new FractalBuildException(ex);
			}
		});
		final FractalRenderer renderer = new StackedFractalRenderer(factories);
		ExitController.getInstance().closeAtExit(renderer);
		
		final FractalComponent view = new SimpleRenderedPanel(renderer, new FractalPart(), new DefaultColoring(), new MandelbrotSet(), 1000);
		new Controller(view);
		final JFrame frame = new JFrame("Fractal");
		frame.add(view.getComponent());
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					ExitController.atExit();
					GPUEnvironment.atExit();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally{
					System.exit(0);
				}
			}
		});
		
		frame.setSize(1800, 1000);
		frame.setVisible(true);
	}
}
