package net.bluetempest.quantum.main;

import net.bluetempest.quantum.Register;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Register psi = new Register(20);
		//psi.hadamardGate(0);
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
		p.test();
		long t2 = System.currentTimeMillis();
		System.out.print("Done in ");
		System.out.println((t2-t1));
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
