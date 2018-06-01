package simon.fractal.rendering;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.rendering.FractalRenderingHandle;

public class RenderMission {
	
	private final FractalImageMap part;
	private final FractalRenderingHandle callback;
	private final FractalFormula formula;
	private final ColoringPattern coloring;
	
	public FractalImageMap getPart() {
		return part;
	}
	
	public FractalRenderingHandle getCallback() {
		return callback;
	}

	public FractalFormula getFormula() {
		return formula;
	}

	public ColoringPattern getColoring() {
		return coloring;
	}
	
	public RenderMission(FractalImageMap part, FractalRenderingHandle callback, FractalFormula formula, ColoringPattern coloring){
		this.part = part;
		this.callback = callback;
		this.formula = formula;
		this.coloring = coloring;
	}
}
