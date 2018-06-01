package simon.fractal.rendering.gpu;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalBuildException;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;
import simon.fractal.rendering.RenderMission;

public class GPUFractalRenderer implements simon.fractal.rendering.FractalRenderer, AutoCloseable{
	
	private static final Logger logger = LoggerFactory.create(GPUFractalRenderer.class);
	
	private FractalFormula currentFormula;
	private AtomicInteger currentAccurancy = new AtomicInteger(20);
	private ColoringPattern currentColoring;
	private Queue<RenderMission> openMissions = new ConcurrentLinkedQueue<>();
	
	private final CLCommandQueue commandQueue;
	private final ExecutorService service;
	private final GPUFractalRendererTask task;

	private AtomicReference<FractalRenderProgram> currentProgram = new AtomicReference<FractalRenderProgram>(null);
	private AtomicReference<FractalRendererException> lastThrownException = new AtomicReference<FractalRendererException>(null);
	private Future<Void> lastMissionResult;
	
	private class GPUFractalRendererTask implements Callable<Void>{

		@Override
		public Void call() throws Exception {
			//since this task is only executed in a single-threaded threadpool, this
			//code is actually executed sequentially, and we do not have to synchronize
			try{
				RenderMission m = openMissions.poll();
				if (m != null){
					currentProgram.get().execute(m, commandQueue, currentAccurancy.get());
				}
			}catch (CLException ex){
				lastThrownException.compareAndSet(null, new FractalRendererException(ex));
			}catch  (FractalRendererException ex){
				lastThrownException.compareAndSet(null, ex);
			}
			return null;
		}
	}
	
	private void checkForException() throws FractalRendererException{
		FractalRendererException lastException = lastThrownException.getAndSet(null);
		if (lastException != null)
			throw lastException;
	}

	@Override
	public void render(FractalImageMap part, FractalRenderingHandle callback) throws FractalRendererException {
		checkForException();
		if (part.getWidth() == 0 || part.getHeight() == 0){
			return;
		}
		openMissions.offer(new RenderMission(part, callback, currentFormula, currentColoring));
		this.lastMissionResult = service.submit(task);
	}

	@Override
	public void cancel() {
		openMissions.clear();
	}

	@Override
	public void waitForFinished() throws FractalRendererException{
		try {
			if (lastMissionResult != null){
				checkForException();
				lastMissionResult.get();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			if (e.getCause() != null){
				if (e.getCause().getClass().equals(CLException.class)){
					throw new FractalRendererException(e.getCause());
				}else if (e.getCause().getClass().equals(FractalRendererException.class)){
					throw (FractalRendererException)e.getCause();
				}
			}
			logger.fatal(e);
			System.exit(0);
		}
	}

	@Override
	public void setFractalFormula(FractalFormula f) {
		this.currentFormula = f;
	}

	@Override
	public void setAccurancy(int accurancy) {
		this.currentAccurancy.set(accurancy);
	}

	@Override
	public void setColoringPattern(ColoringPattern pattern) {
		this.currentColoring = pattern;
	}

	@Override
	public void close() throws Exception {
		logger.info("GPUFractalRenderer closed");
		this.service.shutdown();
		this.service.awaitTermination(1, TimeUnit.DAYS);

		currentProgram.get().close();
		CL10.clReleaseCommandQueue(commandQueue);
	}
	
	public GPUFractalRenderer(RenderProgramFactory builder) throws CLException, FractalBuildException{
		this.currentProgram.set(builder.create());
		this.service = Executors.newFixedThreadPool(1);
		this.commandQueue = GPUEnvironment.getInstance().createCommandQueue();
		this.task = this.new GPUFractalRendererTask();
	}
}
