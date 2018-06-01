package simon.fractal.logging;

import java.io.PrintStream;

import javax.swing.JOptionPane;

public class PrintStreamLogger implements Logger{
	
	private Class<?> caller;
	private PrintStream out;
	private PrintStream err;

	private String callerString(){
		return "[" + caller.getName() + "] ";
	}
	
	@Override
	public void info(String msg) {
		out.println("INFO   : " + callerString() + msg);
	}
	
	private void printFatal(Throwable t){
		err.println("FATAL   : " + callerString() + t.getLocalizedMessage());
		for (StackTraceElement el : t.getStackTrace()){
			err.println("# at " + el.toString());
		}
		if (t.getCause() != null){
			err.print("# Caused by ");
			printFatal(t.getCause());
		}
	}
	
	@Override
	public void fatal(String msg) {
		JOptionPane.showMessageDialog(null, "FATAL ERROR: " + msg + "\nLook in the logs for further information", "Fatal error", JOptionPane.ERROR_MESSAGE);
		err.println("FATAL   : " + callerString() + msg);
		for (StackTraceElement el : Thread.currentThread().getStackTrace()){
			err.println("# at " + el.toString());
		}
	}
	
	@Override
	public void fatal(String msg, Throwable t) {
		JOptionPane.showMessageDialog(null, "FATAL ERROR: " + msg + "\nLook in the logs for further information\nMessage: " + t.getLocalizedMessage(), 
				"Fatal error", JOptionPane.ERROR_MESSAGE);
		err.println("FATAL ERROR: " + msg);
		err.print("Message: ");
		printFatal(t);
	}
	
	@Override
	public void fatal(Throwable t) {
		JOptionPane.showMessageDialog(null, "FATAL ERROR: " + t.getLocalizedMessage() + "\nLook in the logs for further information", "Fatal error", JOptionPane.ERROR_MESSAGE);
		printFatal(t);
	}
	
	@Override
	public void error(String msg, Throwable t) {
		err.println("ERROR   : " + callerString() + msg);
		t.printStackTrace();
	}
	
	@Override
	public void error(Throwable t) {
		err.println("ERROR   : " + callerString());
		t.printStackTrace();
	}

	@Override
	public void error(String msg) {
		err.println("ERROR   : " + callerString() + msg);
	}
	
	@Override
	public void debug(String msg) {
		out.println("DEBUG   : " + callerString() + msg);
	}
	
	public PrintStreamLogger(PrintStream out, PrintStream err, Class<?> caller){
		this.out = out;
		this.err = err;
		this.caller = caller;
	}
}
