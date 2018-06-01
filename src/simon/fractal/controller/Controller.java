package simon.fractal.controller;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputListener;

import simon.fractal.display.FractalComponent;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.formulas.FractalPart;
import simonUtil.complexMath.ComplexNumber;

public class Controller implements MouseInputListener, MouseWheelListener{
	
	private static final String copy = "copyFractalPartToClipboard";
	private static final String paste = "pasteFractalPartFromClipboard";
	private static final String reset = "resetFractalPart";
	private static final String render = "renderCurrentFractalImageMap";
	private FractalComponent comp;
	private ComplexNumber chooseRectangleFirstClick;

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1){
			this.chooseRectangleFirstClick = this.comp.getImageMap().getComplexNumber(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1){
			FractalPart newPart = FractalPart.getContent(chooseRectangleFirstClick, this.comp.getImageMap().getComplexNumber(e.getX(), e.getY()));
			this.comp.setDisplayedPart(newPart);
		}else if (e.getButton() == MouseEvent.BUTTON2){
			FractalImageMap map = this.comp.getImageMap();
			this.comp.setDisplayedPart(map.getFractalPart().setCenter(map.getComplexNumber(e.getX(), e.getY())));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		FractalImageMap map = this.comp.getImageMap();
		this.comp.setDisplayedPart(map.getFractalPart().zoom(e.getWheelRotation() > 0 ? 1.1111 : 0.9));
	}

	public Controller(FractalComponent toControl){
		this.comp = toControl;
		this.comp.getComponent().addMouseListener(this);
		this.comp.getComponent().addMouseWheelListener(this);
		initActions();
	}
	
	private void initActions(){
		KeyStroke copyKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, true);
		this.comp.getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(copyKeyStroke, copy);
		this.comp.getComponent().getActionMap().put(copy, new CopyFractalPartToClipboard(this.comp));
		
		KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, true);
		this.comp.getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pasteKeyStroke, paste);
		this.comp.getComponent().getActionMap().put(paste, new PasteFractalPartFromClipboard(this.comp));
		
		KeyStroke resetKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, true);
		this.comp.getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(resetKeyStroke, reset);
		this.comp.getComponent().getActionMap().put(reset, new ResetFractalPartAction(this.comp));
		
		KeyStroke renderKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK, true);
		this.comp.getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(renderKeyStroke, render);
		this.comp.getComponent().getActionMap().put(render, new RenderFractalImageAction(this.comp));
	}
}
