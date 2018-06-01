package simon.fractal;

import java.util.LinkedList;

import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;

public class ExitController {
	
	private static final Logger logger = LoggerFactory.create(ExitController.class);
	private static ExitController singleton;
	private LinkedList<AutoCloseable> closeAtExit;
	
	public static synchronized ExitController getInstance(){
		if (singleton == null){
			singleton = new ExitController();
		}
		return singleton;
	}
	
	public synchronized void closeAtExit(AutoCloseable object){
		closeAtExit.add(object);
	}
	
	public static synchronized void atExit(){
		if (singleton != null){
			for (AutoCloseable o : singleton.closeAtExit){
				try {
					o.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
	
	private ExitController(){
		closeAtExit = new LinkedList<>();
	}
}
