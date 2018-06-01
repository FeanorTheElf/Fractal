package simon.fractal.formulas;

public class FractalBuildException extends Exception{

	private static final long serialVersionUID = 0L;

	public FractalBuildException(String msg){
		super(msg);
	}
	
	public FractalBuildException(){
		super();
	}
	
	public FractalBuildException(Throwable ex){
		super(ex);
	}
}
