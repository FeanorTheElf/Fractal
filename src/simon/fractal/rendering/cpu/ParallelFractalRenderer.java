package simon.fractal.rendering.cpu;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import simon.fractal.coloring.ColoringPattern;
import simon.fractal.formulas.FractalFormula;
import simon.fractal.formulas.FractalImageMap;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRenderer;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.FractalRenderingHandle;

public class ParallelFractalRenderer implements FractalRenderer, RendererTaskStatusChangeListener{
	
	private static final Logger logger = LoggerFactory.create(ParallelFractalRenderer.class);

	private int threadCount;
	private ExecutorService service;
	private RendererTask[] tasks;
	private Future<?>[] futures;
	private double[] progress;

	private BufferedImage result;
	
	private long startTime;
	private FractalRenderingHandle callback;

	public void render(FractalImageMap part, FractalRenderingHandle callback) throws FractalRendererException {
		waitForFinished();
		this.callback = callback;
		if (result == null || part.getWidth() != result.getWidth() || part.getHeight() != result.getHeight()){
			logger.info("Creating new image");
			result = new BufferedImage(part.getWidth(), part.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		this.startTime = System.currentTimeMillis();
		FractalImageMap[] maps = part.divide(threadCount);
		for (int i = 0; i < threadCount; i++){
			tasks[i].init(maps[i], part.getWidth(), (DataBufferInt)result.getRaster().getDataBuffer(), i);
			futures[i] = service.submit(tasks[i]);
		}
	}
	
	@Override
	public void cancel(){
		for (RendererTask task : tasks){
			task.interrupt();
		}
		waitForFinished();
	}
	
	@Override
	public void waitForFinished(){
		if (this.futures[0] == null){ //if the futures have not been initialized yet
			return;
		}
		try {
			for (Future<?> future : futures){
				future.get();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void renderTaskStatusChanged(int id, double newStatus){
		this.progress[id] = newStatus;
		informListeners();
	}
	
	private void informListeners(){
		double totalProgress = 0;
		boolean allDone = true;
		for (int i = 0; i < progress.length; i++){
			totalProgress += progress[i];
			allDone &= progress[i] == 1;
		}
		if (allDone){
			logger.info("Rendered in " + (System.currentTimeMillis() - startTime));
			this.callback.calculationFinished(this.result);
		}else{
			this.callback.statusChange(totalProgress / progress.length);
		}
	}
	
	public ParallelFractalRenderer(){
		this.threadCount = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(threadCount);
		tasks = new RendererTask[threadCount];
		for (int i = 0; i < threadCount; i++){
			tasks[i] = new RendererTask();
			tasks[i].setRendererTaskStatusChangedListener(this);
		}
		progress = new double[threadCount];
		futures = new Future[threadCount];
	}
	
	@Override
	public void renderstatusChanged(int id, double percentage) {
		this.progress[id] = percentage;
		informListeners();
	}

	@Override
	public void renderingFinished(int id) {
		this.progress[id] = 1;
		informListeners();
	}

	@Override
	public void close() throws Exception {
		cancel();
		service.shutdown();
		service.awaitTermination(1, TimeUnit.DAYS);
	}
	
	@Override
	public void setFractalFormula(FractalFormula f) {
		for (RendererTask task : this.tasks){
			task.setFractal(f);
		}
	}

	@Override
	public void setAccurancy(int accurancy) {
		for (RendererTask task : this.tasks){
			task.setAccurancy(accurancy);
		}
	}

	@Override
	public void setColoringPattern(ColoringPattern pattern) {
		for (RendererTask task : this.tasks){
			task.setColoring(pattern);
		}
	}
}
