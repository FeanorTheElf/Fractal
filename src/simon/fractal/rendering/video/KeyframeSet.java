package simon.fractal.rendering.video;

import java.util.Iterator;
import java.util.LinkedList;

import simon.fractal.formulas.FractalPart;

public class KeyframeSet implements Iterable<FractalPart>{

	private LinkedList<FractalPart> keyframes;
	
	public void addKeyframe(FractalPart p){
		this.keyframes.add(p);
	}
	
	public boolean hasKeyframes(){
		return !keyframes.isEmpty();
	}
	
	public FractalPart getLastKeyframe(){
		return keyframes.getLast();
	}
	
	public FractalPart getKeyframe(int i){
		if (i < 0 || i >= this.keyframes.size()){
			return null;
		}
		return this.keyframes.get(i);
	}
	
	public int getKeyframeNumber(){
		return keyframes.size();
	}
	
	public KeyframeSet(){
		this.keyframes = new LinkedList<>();
	}

	@Override
	public Iterator<FractalPart> iterator() {
		return this.keyframes.iterator();
	}
}
