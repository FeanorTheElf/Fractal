package simon.fractal.rendering;

import java.awt.Image;

public interface FractalRenderingHandle extends FractalRenderingCallback, FractalRenderingStatusChangeListener{

	public static FractalRenderingHandle makeHandle(FractalRenderingStatusChangeListener listener, FractalRenderingCallback callback){
		return new FractalRenderingHandle() {
			
			@Override
			public void statusChange(double currentPercentage) {
				listener.statusChange(currentPercentage);
			}
			
			@Override
			public void calculationFinished(Image image) {
				callback.calculationFinished(image);
			}
		};
	}
	
	public static FractalRenderingHandle makeHandle(FractalRenderingCallback callback){
		return makeHandle(percentage -> {}, callback);
	}
}
