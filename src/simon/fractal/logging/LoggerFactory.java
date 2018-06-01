package simon.fractal.logging;

public class LoggerFactory {

	public static Logger create(final Class<?> caller){
		return new PrintStreamLogger(System.out, System.err, caller);
	}
}
