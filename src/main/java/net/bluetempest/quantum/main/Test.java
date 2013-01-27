package net.bluetempest.quantum.main;

import net.bluetempest.quantum.Register;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Register psi = new Register(16);
		
		
		long t11 = System.currentTimeMillis();
		psi.hadamardGate(0);
		psi.hadamardGate(1);
		psi.hadamardGate(2);
		psi.hadamardGate(3);
		psi.hadamardGate(4);
		psi.hadamardGate(5);
		psi.hadamardGate(6);
		psi.hadamardGate(7);
		psi.hadamardGate(8);
		psi.hadamardGate(9);
		psi.hadamardGate(10);
		psi.hadamardGate(11);
		psi.hadamardGate(12);
		psi.hadamardGate(13);
		long t22 = System.currentTimeMillis();
		System.out.print("Done in ");
		System.out.println((t22-t11));
		
		//System.out.println("Done 1");
		//for(int i=0;i<psi.amplitudes.length;i++) {
		//	System.out.println(psi.amplitudes[i]);
		//}
		/*
		for(int i=0;i<10;i++) {
			Psi register = new Psi(20);
			for(int j=0;j<16;j++) {
				register.hadamardGate(j);
			}
			//register.hadamardGate(0);
			//register.controlledNot(0,1);
			//System.out.println(arrToString(register.getWeights()));
			
			//register.phaseGate(0, 8);
			//register.phaseGate(1, 8);
			//register.controlledNot(0, 1);
			System.out.println(arrToString(register.collapse()));
		}
		*/
		
		Register p = new Register(20);
		
		long t1 = System.currentTimeMillis();
		p.initOpencl();
		long t3 = System.currentTimeMillis();
		p.HadamardOpencl(0);
		p.HadamardOpencl(1);
		p.HadamardOpencl(2);
		p.HadamardOpencl(3);
		p.HadamardOpencl(4);
		p.HadamardOpencl(5);
		p.HadamardOpencl(6);
		p.HadamardOpencl(7);
		p.HadamardOpencl(8);
		p.HadamardOpencl(9);
		p.HadamardOpencl(10);
		p.HadamardOpencl(11);
		p.HadamardOpencl(12);
		p.HadamardOpencl(13);
		long t2 = System.currentTimeMillis();
		p.commitOpencl();
		
		//System.out.println(arrToString(p.getWeights()));

		System.out.print("Done in ");
		System.out.println((t2-t1));
		System.out.print("Kernel in ");
		System.out.println((t3-t1));
	}
	
	private static String arrToString(float[] arr) {
		String ret = "(";
		for(int i=0;i<arr.length;i++) {
			ret = ret + String.valueOf( arr[i] );
			if(i != arr.length-1) {
				ret += ",";
			}
		}
		ret += ")";
		return ret;
	}

}
