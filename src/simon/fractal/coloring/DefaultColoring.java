package simon.fractal.coloring;

public class DefaultColoring extends ColoringClass{

	@Override
	public int getColor(int iterations) {
		if (iterations == -1){
			return 0xFF000000;
		}
		return (iterations << ((iterations >> 5) & 0xFF)) | 0xFF000000;
	}

	@Override
	public String getId() {
		return "default";
	}

	@Override
	public String[] getFormula() {
		return new String[]{
			"0xFF000000",
			"(i << ((i >> 5) & 0xFF)) | 0xFF000000"
		};
	}

	@Override
	public int getParameterCount() {
		return 0;
	}

}
