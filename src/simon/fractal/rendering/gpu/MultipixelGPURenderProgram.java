package simon.fractal.rendering.gpu;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLProgram;

import simon.fractal.formulas.FractalBuildException;
import simon.fractal.logging.Logger;
import simon.fractal.logging.LoggerFactory;
import simon.fractal.rendering.FractalRendererException;
import simon.fractal.rendering.RenderMission;

public class MultipixelGPURenderProgram implements FractalRenderProgram{
	
	private static final Logger logger = LoggerFactory.create(SimpleFractalRenderProgram.class);
	private static final String ls = System.lineSeparator();
	private static final String programTemplate = 
			"#pragma OPENCL EXTENSION cl_khr_fp64 : enable" + ls + 
			"" + ls + 
			"__kernel void iteration_count(global int * resultRaw," + ls + 
			"		const double complexUnitXPerPixel, const double complexUnitYPerPixel," + ls + 
			"		const double top_left_real, const double top_left_imaginary," + ls + 
			"		const unsigned int width, const unsigned int max_iterations, const unsigned int size)" + ls + 
			"{" + ls + 
			"	unsigned int item_id = get_global_id(0);" + ls + 
			"	global int16 * result = (global int16*)resultRaw;" + ls + 
			"	" + ls + 
			"	double16 z_real;" + ls + 
			"	double16 z_im;" + ls + 
			"	double16 new_z_real;" + ls + 
			"	int16 entries_to_set;" + ls + 
			"	" + ls + 
			"	while (item_id < size){" + ls + 
			"		const double16 c_real = convert_double16(((uint16)(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15) + item_id * 16) % width) * complexUnitXPerPixel + top_left_real;" + ls + 
			"		const double16 c_im = convert_double16(((uint16)(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15) + item_id * 16) / width) * complexUnitYPerPixel + top_left_imaginary;" + ls + 
			"		" + ls + 
			"		z_real = 0;" + ls + 
			"		z_im = 0;" + ls + 
			"		entries_to_set = (int16)-1;" + ls + 
			"		" + ls + 
			"		for (unsigned int i = 1; i < max_iterations; ++i){" + ls + 
			"			new_z_real = z_real * z_real - z_im * z_im + c_real;" + ls + 
			"			z_im = 2 * z_real * z_im + c_im;" + ls + 
			"			z_real = new_z_real;" + ls + 
			"			" + ls + 
//			"			result[item_id] = (((int16)0xFFFF0000) & entries_to_set) | (result[item_id] & (~entries_to_set));" + ls +
			"			result[item_id] = (((int16)((i << ((i >> 5) & 0xFF)) | 0xFF000000)) & entries_to_set) | (result[item_id] & (~entries_to_set));" + ls +
			"			entries_to_set &= convert_float16(z_im * z_im + z_real * z_real) < 4;" + ls + 
			"		}" + ls + 
			"		result[item_id] = (((int16)0xFF000000) & entries_to_set) | (result[item_id] & (~entries_to_set));" + ls + 
			"		" + ls + 
			"		item_id += get_global_size(0);" + ls + 
			"	}" + ls + 
			"}";
	
	private static class BufferedWritableRaster extends WritableRaster{
		
		public BufferedWritableRaster(SampleModel sm, DataBuffer dbuf) {
			super(sm, dbuf, new Point());
		}
	}

	private CLProgram program;
	private CLKernel kernel;
	private IntBuffer errorBuffer;
	private PointerBuffer workDimBuffer;
	
	private RenderMission current;
	
	private IntBuffer resultBuffer;
	private BufferedImage result;
	private CLMem gpuBuffer;
	
	private long startTime;
	
	@Override
	public void close() {
		if (gpuBuffer != null)
			CL10.clReleaseMemObject(gpuBuffer);
		if (kernel != null)
			CL10.clReleaseKernel(kernel);
		if (program != null)
			CL10.clReleaseProgram(program);
	}
	
	private boolean mustRebuildAll(RenderMission m){
		return current == null || !Objects.equals(m.getFormula(), current.getFormula()) || !Objects.equals(m.getColoring(), current.getColoring());
	}
	
	private boolean mustRebuildMem(RenderMission m){
		return m.getPart().getPixelCount() != current.getPart().getPixelCount();
	}
	
	private static long roundUpToMulitpleOf16(long n){
		return (((n - 1) >> 4) + 1) << 4;
	}
	
	private static long roundUpDivide16(long n){
		return ((n - 1) >> 4) + 1;
	}
	
	private void rebuildAll(RenderMission m) throws CLException, FractalBuildException{
		logger.info("Rebuilding program");
//		String[] formulaStrings = m.getFormula().getFormula();
//		String[] coloringStrings = m.getColoring().getFormula();
		String src = programTemplate
//				.replace("$INIT$", formulaStrings[0])
//				.replace("$STEP$", formulaStrings[1])
//				.replace("$COLOR_CONV$", coloringStrings[0])
//				.replace("$COLOR_DIV$", coloringStrings[1])
				;
		if (program != null)
			CL10.clReleaseProgram(program);
		program = CL10.clCreateProgramWithSource(GPUEnvironment.getInstance().getContext(), src, errorBuffer);
		GPUEnvironment.checkError(errorBuffer, "Error creating CLProgram");
		int status = CL10.clBuildProgram(program, GPUEnvironment.getInstance().getDevice(), "", null);
		
		if (status == CL10.CL_BUILD_PROGRAM_FAILURE || status == CL10.CL_INVALID_PROGRAM){
			System.out.println(program.getBuildInfoString(GPUEnvironment.getInstance().getDevice(), CL10.CL_PROGRAM_BUILD_LOG));
			throw new CLException("OpenCL program syntax error");
		}else if (status != CL10.CL_SUCCESS){
			throw new CLException("Error while building openCL program");
		}
		if (kernel != null)
			CL10.clReleaseKernel(kernel);
		kernel = CL10.clCreateKernel(program, "iteration_count", errorBuffer);
		GPUEnvironment.checkError(errorBuffer, "Error creating CLKernel");
		
		rebuildMem(m);
	}
	
	private void rebuildMem(RenderMission m) throws CLException {
		logger.info("Reallocating memory");
		int pixelCount = m.getPart().getPixelCount();
		this.resultBuffer = BufferUtils.createIntBuffer(pixelCount);
		this.result = createImage(m.getPart().getWidth(), m.getPart().getHeight(), this.resultBuffer);
		
		if (gpuBuffer != null)
			CL10.clReleaseMemObject(gpuBuffer);
		this.gpuBuffer = CL10.clCreateBuffer(GPUEnvironment.getInstance().getContext(), CL10.CL_MEM_READ_WRITE, 
				roundUpToMulitpleOf16(pixelCount) * Integer.BYTES, errorBuffer);
		GPUEnvironment.checkError(errorBuffer, "Error allocating memory");
		this.workDimBuffer.put(0, roundUpDivide16(pixelCount));
	}

	private BufferedImage createImage(int width, int height, IntBuffer buffer){
		DataBuffer dbuf = new DataBuffer(DataBuffer.TYPE_INT, width * height) {
            public void setElem(int bank, int i, int val) {
            	buffer.put(i, val);
            }
            public int getElem(int bank, int i) {
                return buffer.get(i);
            }
        };
        ColorModel cm = ColorModel.getRGBdefault();
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        WritableRaster raster = new BufferedWritableRaster(sm, dbuf);
        return new BufferedImage(cm, raster, false, new Hashtable<>());
	}

	@Override
	public void execute(RenderMission m, CLCommandQueue queue, int accurancy) throws CLException, FractalRendererException {
		if (mustRebuildAll(m)){
			try {
				rebuildAll(m);
			} catch (FractalBuildException e) {
				throw new FractalRendererException(e);
			}
		}else if (mustRebuildMem(m)){
			rebuildMem(m);
		}
		current = m;
		
		startTime = System.currentTimeMillis();
		int maxIterations = -(int)(Math.log10(m.getPart().getScale()) * accurancy);
		kernel.setArg(0, gpuBuffer);
		kernel.setArg(1, m.getPart().getComplexUnitsXPerPixel());
		kernel.setArg(2, m.getPart().getComplexUnitsYPerPixel());
		kernel.setArg(3, m.getPart().getFractalPart().getTopLeft().getReal());
		kernel.setArg(4, m.getPart().getFractalPart().getTopLeft().getImaginary());
		kernel.setArg(5, m.getPart().getWidth());
		kernel.setArg(6, maxIterations);
		kernel.setArg(7, (int)roundUpDivide16(m.getPart().getPixelCount()));
		CL10.clEnqueueNDRangeKernel(queue, kernel, 1, null, workDimBuffer, null, null, null);
		CL10.clEnqueueReadBuffer(queue, gpuBuffer, CL10.CL_TRUE, 0, resultBuffer, null, null);
		logger.info("Rendered in " + (System.currentTimeMillis() - startTime) + " ms");
		m.getCallback().calculationFinished(result);
	}

	public MultipixelGPURenderProgram() throws CLException, FractalBuildException{
		errorBuffer = BufferUtils.createIntBuffer(1);
		this.workDimBuffer = BufferUtils.createPointerBuffer(1);
	}
}
