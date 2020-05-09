package simon.fractal.coloring;

public class BlackWhiteColoring extends ColoringClass{

	@Override
	public int getColor(int iterations) {
		if (iterations == -1){
			return 0xFF000000;
		}
		return 0xFFFFFFFF;
	}

	@Override
	public String getId() {
		return "default";
	}

	@Override
	public String[] getFormula() {
		return new String[]{
			"0xFF000000",
			"0xFFFFFFFF"
		};
	}

	@Override
	public int getParameterCount() {
		return 0;
	}

}
