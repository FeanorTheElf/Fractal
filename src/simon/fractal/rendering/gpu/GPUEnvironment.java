package simon.fractal.rendering.gpu;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.OpenCLException;
import org.lwjgl.opencl.Util;

public class GPUEnvironment{

	private final int addressBits;
	private final int workItemDimensions;
	private final long[] maxWorkItemCount;
	
	private static GPUEnvironment singleton;
	
	private CLDevice device;
	private CLContext context;
	private IntBuffer errorBuffer;
	

	private int getDeviceWorkItemDimensions(){
		ByteBuffer res = BufferUtils.createByteBuffer(4);
		CL10.clGetDeviceInfo(getDevice(), CL10.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS, res, null);
		res.rewind();
		return res.getInt();
	}
	
	private int getDeviceAddressBits(){
		ByteBuffer res = BufferUtils.createByteBuffer(4);
		CL10.clGetDeviceInfo(getDevice(), CL10.CL_DEVICE_ADDRESS_BITS, res, null);
		res.rewind();
		return res.getInt();
	}
	
	private long[] getDeviceMaxWorkItemCount() throws CLException{
		long[] result = new long[workItemDimensions];
		if (getDeviceAddressBits() == 32){
			ByteBuffer res = BufferUtils.createByteBuffer(4 * result.length);
			CL10.clGetDeviceInfo(getDevice(), CL10.CL_DEVICE_MAX_WORK_ITEM_SIZES, res, null);
			res.rewind();
			for (int i = 0; i < result.length; ++i){
				result[i] = res.getInt();
			}
		}else if (getDeviceAddressBits() == 64){
			ByteBuffer res = BufferUtils.createByteBuffer(8 * result.length);
			CL10.clGetDeviceInfo(getDevice(), CL10.CL_DEVICE_MAX_WORK_ITEM_SIZES, res, null);
			res.rewind();
			for (int i = 0; i < result.length; ++i){
				result[i] = res.getLong();
			}
		}else{
			throw new CLException("Only 64 and 32-bit GPU architectures are supported");
		}
		return result;
	}
	
	private GPUEnvironment() throws CLException{
		try {
			CL.create();
		} catch (LWJGLException ex) {
			throw new CLException("Error initializing global CL context", ex);
		} catch (LinkageError err){
			throw new CLException("Error locating OpenCL library. This error might occur because\n"
					+ "- you do not have a graphic board\n"
					+ "- your graphic board does not support the OpenCL interface\n"
					+ "- the OpenCL natives are not available or not correctly linked\n"
					+ "- the LWJGL java library cannot be located", err);
		}
		
		errorBuffer = BufferUtils.createIntBuffer(1);
		CLPlatform platform = CLPlatform.getPlatforms().get(0);
		List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
		device = devices.get(0);
		
		addressBits = getDeviceAddressBits();
		workItemDimensions = getDeviceWorkItemDimensions();
		maxWorkItemCount = getDeviceMaxWorkItemCount();
		
		try {
			context = CLContext.create(platform, devices, errorBuffer);
			checkError(errorBuffer, "Error creating CLContext");
		} catch (LWJGLException ex) {
			if (context != null){
				CL10.clReleaseContext(context);
			}
			throw new CLException(ex);
		}
	}
	
	public static GPUEnvironment getInstance() throws CLException{
		if (singleton == null){
			singleton = new GPUEnvironment();
		}
		return singleton;
	}
	
	public static void atExit(){
		if (singleton != null){
			singleton.close();
		}
	}
	
	public static void checkError(IntBuffer errorBuffer, String msg) throws CLException{
		try{
			Util.checkCLError(errorBuffer.get(0));
		}catch (OpenCLException e) {
			throw new CLException(msg, e);
		}
	}
	
	public CLCommandQueue createCommandQueue() throws CLException{
		CLCommandQueue queue = CL10.clCreateCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE, errorBuffer);
		checkError(errorBuffer, "Error creating CLCommandQueue");
		return queue;
	}

	public CLContext getContext(){
		return context;
	}

	private void close() {
		CL10.clReleaseContext(context);
		CL.destroy();
	}

	public CLDevice getDevice() {
		return device;
	}

	public int getAddressBits() {
		return addressBits;
	}

	public long[] getMaxWorkItemCount() {
		return maxWorkItemCount;
	}

	public int getWorkItemDimensions() {
		return workItemDimensions;
	}
}
