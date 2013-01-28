package net.bluetempest.quantum.main;

import net.bluetempest.quantum.Register;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Register psi = new Register(2);
		
		
		long t11 = System.currentTimeMillis();
		psi.phaseGate(0,8);
		psi.phaseGate(1,8);
		/*
		psi.phaseGate(2,8);
		psi.phaseGate(3,8);
		psi.phaseGate(4,8);
		psi.phaseGate(5,8);
		psi.phaseGate(6,8);
		psi.phaseGate(7,8);
		psi.phaseGate(8,8);
		psi.phaseGate(9,8);
		psi.phaseGate(10,8);
		psi.phaseGate(11,8);
		psi.phaseGate(12,8);
		psi.phaseGate(13,8);
		*/
		long t22 = System.currentTimeMillis();
		System.out.print(arrToString(psi.getWeights()));
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
				register.phaseGate(j);
			}
			//register.phaseGate(0);
			//register.controlledNot(0,1);
			//System.out.println(arrToString(register.getWeights()));
			
			//register.phaseGate(0, 8);
			//register.phaseGate(1, 8);
			//register.controlledNot(0, 1);
			System.out.println(arrToString(register.collapse()));
		}
		*/
		
		Register p = new Register(2);
		
		long t1 = System.currentTimeMillis();
		p.initOpencl();
		long t3 = System.currentTimeMillis();
		p.phaseOpencl(0,8);
		p.phaseOpencl(1,8);
		/*
		p.phaseOpencl(2,8);
		p.phaseOpencl(3,8);
		p.phaseOpencl(4,8);
		p.phaseOpencl(5,8);
		p.phaseOpencl(6,8);
		p.phaseOpencl(7,8);
		p.phaseOpencl(8,8);
		p.phaseOpencl(9,8);
		p.phaseOpencl(10,8);
		p.phaseOpencl(11,8);
		p.phaseOpencl(12,8);
		p.phaseOpencl(13,8);
		*/
		long t2 = System.currentTimeMillis();
		p.commitOpencl();
		
		//System.out.println(arrToString(p.getWeights()));

		System.out.print("Done in ");
		System.out.println((t2-t1));
		System.out.print("Kernel in ");
		System.out.println((t3-t1));
		System.out.println(arrToString(p.getWeights()));
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
