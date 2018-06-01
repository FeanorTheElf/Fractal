package simon.fractal.rendering.cpu;

interface RendererTaskStatusChangeListener {

	void renderstatusChanged(int id, double percentage);
	void renderingFinished(int id);
	
}
