package net.bluetempest.quantum;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Random;

import net.bluetempest.math.Complex;
import net.bluetempest.quantum.gates.Gates;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;

public class Register {
	
	public Complex[] amplitudes;
	public int qubitCount;
	
	private boolean openclInitialized = false;
	private boolean openclEnabled = false;
	
	private CLBuffer<Float> realExisting;
	private CLBuffer<Float> imagExisting;
	private CLBuffer<Float> realOutput;
	private CLBuffer<Float> imagOutput;
	private CLEvent lastEvent;
	
	private CLContext context;
	private CLQueue queue;
	private Gates kernels;
	
	
	public Register(int qubits) {
		qubitCount = qubits;
		setZeroState();
		this.amplitudes[0] = new Complex(1f,0);
	}
	
	private void setZeroState() {
		int ampSize = (int) Math.pow(2,qubitCount); // might switch to bitshift for openCL
		amplitudes = new Complex[ampSize];
		for(int i=0;i<amplitudes.length;i++)
			amplitudes[i] = new Complex(0,0);
	}
	
	public void initOpencl() {
		context = JavaCL.createBestContext();
        queue = context.createDefaultQueue();
        
        ByteOrder byteOrder = context.getByteOrder();
        
        Pointer<Float>
        realPtr = Pointer.allocateFloats(amplitudes.length).order(byteOrder),
        imagPtr = Pointer.allocateFloats(amplitudes.length).order(byteOrder);
		
        for (int i = 0; i < amplitudes.length; i++) {
            realPtr.set(i, amplitudes[i].getReal());
            imagPtr.set(i, amplitudes[i].getImaginary());
        }
		
        realExisting = context.createFloatBuffer(Usage.InputOutput, realPtr);
        imagExisting = context.createFloatBuffer(Usage.InputOutput, imagPtr);
        realOutput = context.createFloatBuffer(Usage.InputOutput, amplitudes.length);
        imagOutput = context.createFloatBuffer(Usage.InputOutput, amplitudes.length);
        
        try {
			kernels = new Gates(context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        openclInitialized = true;
	}
	
	public void HadamardOpencl(int qubit) { 
		lastEvent = kernels.hadamardGate(queue, realExisting, imagExisting, realOutput, imagOutput, qubit, amplitudes.length,new int[] {amplitudes.length}, null,lastEvent);
		swapOpenclBuffers();
	}
	
	private void swapOpenclBuffers() {
		CLBuffer<Float> tReal = realExisting;
		CLBuffer<Float> tImag = imagExisting;
		realExisting = realOutput;
		imagExisting = imagOutput;
		realOutput = tReal;
		imagOutput = tImag;
	}
	
	public Complex[] getAmplitudes() {
		return amplitudes;
	}
	
	/**
	 * Rotates qubit by pi / phase radians
	 * TODO Needs to be implemented with complexes
	 * 
	 * @param qubit
	 */
	public void phaseGate(int qubit, int phase) {
		for(int i=0;i<amplitudes.length;i++) {
			if(indexRepresents(qubit, i) == 0) {
				// Do negative power thing
				Complex c = new Complex(0, -1);
				c.mul(new Complex(Complex.PI/phase, 0));
				amplitudes[i] = amplitudes[i].mul(c.exp());
			} else {
				// do positive power thing
				Complex c = new Complex(0, 1);
				c.mul(new Complex(Complex.PI/phase, 0));
				amplitudes[i] = amplitudes[i].mul(c.exp());
			}
		}
	}
	
	public void hadamardGate(int qubit) {
		Complex[] existingAmplitudes = amplitudes.clone();
		for(int i=0;i<amplitudes.length;i++) {
			if(indexRepresents(qubit,i) == 0) {
				amplitudes[i] = (existingAmplitudes[i].sub(existingAmplitudes[i +(int) Math.pow(2, qubit)])).div(new Complex((float)Math.sqrt(2), 0.0f));
			} else {
				amplitudes[i] = (existingAmplitudes[i - (int) Math.pow(2, qubit)].sub(existingAmplitudes[i])).div(new Complex((float)Math.sqrt(2), 0.0f));
			}
		}
	}
	
	public void controlledNot(int qubitControl, int qubitTransform) {
		Complex[] existingAmplitudes = amplitudes.clone();
		for(int i=0;i<amplitudes.length;i++) {
			amplitudes[i ^ (((i >> qubitControl) % 2) << qubitTransform)] = existingAmplitudes[i];
		}
	}
	
	/**
	 * Returns whether this index in the amplitudes array would be a 0 or 1
	 * @param index
	 * @return
	 */
	private int indexRepresents(int qubit, int index) {
		return (index >> qubit) % 2;
	}
	
	public float[] collapse() {
		Random rand = new Random();
		float[] weights = getWeights();
		
		
		
		for(int i=0;i<amplitudes.length;i++) {
			float abs = amplitudes[i].abs().getReal();
			weights[i] = abs*abs;
		}
		
		float sum = 0;
		for(int i=0;i<weights.length;i++) 
			sum += weights[i];
		float choice = rand.nextFloat() * sum;
		
		for(int i=0;i<weights.length;i++) {
			choice -= weights[i];
			if(choice < 0) {
				setZeroState();
				amplitudes[i] = new Complex(1.0f, 0);
				float[] ret = new float[qubitCount]; 
				for(int j=0;j<qubitCount;j++) {
					ret[j] = (i>>j) % 2;
				}
				return ret;
			}
		}
		
		return null;
		
	}

	public float[] getWeights() {
		float[] weights = new float[amplitudes.length];

		for (int i = 0; i < amplitudes.length; i++) {
			float abs = amplitudes[i].abs().getReal();
			weights[i] = abs * abs;
		}
		return weights;
	}
	
	public void test() {
        
        
	}
	
	public void commitOpencl() {
		Pointer<Float> realOutPtr = realExisting.read(queue, lastEvent);
		Pointer<Float> imagOutPtr = imagExisting.read(queue, lastEvent);
		
		float[] arr = (float[]) realOutPtr.getArray();
		float[] arr2 = (float[]) imagOutPtr.getArray();
		
		for(int i=0;i<arr.length;i++) {
			amplitudes[i] = new Complex(arr[i],arr2[i]);
		}
		
	}

}
