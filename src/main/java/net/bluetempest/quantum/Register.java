package net.bluetempest.quantum;

import gates.Hadamard;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Random;

import net.bluetempest.math.Complex;

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
		CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();
        
        Pointer<Float>
        realPtr = Pointer.allocateFloats(amplitudes.length).order(byteOrder),
        imagPtr = Pointer.allocateFloats(amplitudes.length).order(byteOrder);
		
        for (int i = 0; i < amplitudes.length; i++) {
            realPtr.set(i, amplitudes[i].getReal());
            imagPtr.set(i, amplitudes[i].getImaginary());
        }
        
        CLBuffer<Float> 
        realBuffer = context.createBuffer(Usage.Input, realPtr),
        imagBuffer = context.createBuffer(Usage.Input, imagPtr);
        
        CLBuffer<Float> realOut = context.createFloatBuffer(Usage.Output, amplitudes.length);
        CLBuffer<Float> imagOut = context.createFloatBuffer(Usage.Output, amplitudes.length);
        
        try {
			Hadamard kernels = new Hadamard(context);
			CLEvent addEvt = kernels.hadamardGate(queue, realBuffer, imagBuffer, realOut, imagOut, 0, amplitudes.length,new int[] {amplitudes.length}, null);
			Pointer<Float> realOutPtr = realOut.read(queue, addEvt);
			Pointer<Float> imagOutPtr = imagOut.read(queue, addEvt);
			float[] arr = (float[]) realOutPtr.getArray();
			float[] arr2 = (float[]) imagOutPtr.getArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load source");
		}
	}

}
