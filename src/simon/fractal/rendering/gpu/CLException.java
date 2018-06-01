package simon.fractal.rendering.gpu;

public class CLException extends Exception {

	private static final long serialVersionUID = 0L;

	public CLException(String msg){
		super(msg);
	}
	
	public CLException(){
		super();
	}
	
	public CLException(Throwable ex){
		super(ex);
	}
	
	public CLException(String msg, Throwable ex){
		super(msg, ex);
	}
}
