package simon.fractal.rendering;

public class FractalRendererException extends Exception{

	private static final long serialVersionUID = 1L;

	public FractalRendererException(String msg){
		super(msg);
	}
	
	public FractalRendererException(){
		super();
	}
	
	public FractalRendererException(Throwable ex){
		super(ex);
	}
}
