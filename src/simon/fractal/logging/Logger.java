package simon.fractal.logging;

public interface Logger {

	void debug(String msg);
	void info(String msg);
	
	void error(Throwable t);
	void error(String msg);
	void error(String msg, Throwable t);
	
	void fatal(Throwable t);
	void fatal(String msg);
	void fatal(String msg, Throwable t);
}
