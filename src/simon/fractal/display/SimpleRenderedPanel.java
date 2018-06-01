package simon.fractal.display;

import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.formulas.FractalPart;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;

public class SimpleRenderedPanel extends JPanel implements ComponentListener, FractalRenderingHandle, FractalComponent{

	private static final long serialVersionUID = 1L;
	
	private FractalRenderer r;
	private Image image;
	private FractalPart currentPart;
	
	private FractalFormula formula;
	private int accurancy;
	private ColoringPattern coloring;
	
	protected void paintComponent(java.awt.Graphics g){
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}
	
	private double getWidthToHeight(){
		return (double)this.getWidth() / (double)this.getHeight();
	}
	
	private synchronized void rerender(){
		r.cancel();
		try {
			r.render(currentPart.mapTo(this.getWidth(), this.getHeight()), this);
		} catch (FractalRendererException e) {
			e.printStackTrace();
		}
	}

	public SimpleRenderedPanel(FractalRenderer r, FractalPart initialPart, ColoringPattern initialColoring, FractalFormula initialFormula, int initialAccurancy){
		this.r = r;
		this.currentPart = initialPart;
		this.formula = initialFormula;
		this.coloring = initialColoring;
		this.accurancy = initialAccurancy;
		this.r.setFractalFormula(initialFormula);
		this.r.setColoringPattern(initialColoring);
		this.r.setAccurancy(initialAccurancy);
		this.addComponentListener(this);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		this.currentPart = currentPart.changeSideRelation(this.getWidthToHeight());
		rerender();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
		rerender();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void statusChange(double currentPercentage) {
	}

	@Override
	public void calculationFinished(Image image) {
		this.image = image;
		repaint();
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void setFormula(FractalFormula formula) {
		this.formula = formula;
		this.r.setFractalFormula(formula);
		this.rerender();
	}

	@Override
	public void setDisplayedPart(FractalPart part) {
		this.currentPart = part.changeSideRelation(this.getWidthToHeight());
		this.rerender();
	}

	@Override
	public void setColoringPattern(ColoringPattern pattern) {
		this.coloring = pattern;
		this.r.setColoringPattern(pattern);
		this.rerender();
	}

	@Override
	public void setAccurancy(int accurancy) {
		this.accurancy = accurancy;
		this.r.setAccurancy(accurancy);		
		this.rerender();
	}

	@Override
	public FractalImageMap getImageMap() {
		return currentPart.mapTo(this.getWidth(), this.getHeight());
	}

	@Override
	public FractalFormula getFormula() {
		return formula;
	}

	@Override
	public int getAccurancy() {
		return accurancy;
	}

	@Override
	public ColoringPattern getColoringPattern() {
		return coloring;
	}
}
