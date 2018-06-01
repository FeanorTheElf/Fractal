package simon.fractal.rendering.cpu;

import java.awt.image.DataBufferInt;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simonUtil.util.IntTupel;

class RendererTask implements Runnable{

	private int id;
	
	private FractalFormula f;
	private int accurancy;
	private FractalImageMap map;
	private int totalWidth;
	private ColoringPattern coloring;
	
	private DataBufferInt result;
	private volatile boolean interrupted = false;
	
	private RendererTaskStatusChangeListener master;
	
	public void setRendererTaskStatusChangedListener(RendererTaskStatusChangeListener l){
		this.master = l;
	}
	
	@Override
	public void run() {
		interrupted = false;
		int maxIterations = -(int)(Math.log10(map.getScale()) * this.accurancy);
		for (IntTupel p : map){
			if (interrupted){
				return;
			}
			fillPixel(p.a, p.b, maxIterations);
		}
		this.master.renderingFinished(id);
	}

	private void fillPixel(int x, int y, int maxIterations){
		int iterCount = f.getIterationCount(map.getComplexNumber(x, y), maxIterations);
		result.setElem(y * totalWidth + x, coloring.getColor(iterCount));
		if (x == map.getStartX()){
			double status = (double)(y - map.getStartY()) / (map.getHeight());
			this.master.renderstatusChanged(id, status);
		}
	}
	
	public void setFractal(FractalFormula f){
		this.f = f;
	}
	
	public void setAccurancy(int accurancy){
		this.accurancy = accurancy;
	}
	
	public void setColoring(ColoringPattern coloring){
		this.coloring = coloring;
	}
	
	public void interrupt(){
		this.interrupted = true;
	}
	
	public void init(FractalImageMap map, int totalWidth, DataBufferInt result, int id){
		this.map = map;
		this.result = result;
		this.id = id;
		this.totalWidth = totalWidth;
	}
}
