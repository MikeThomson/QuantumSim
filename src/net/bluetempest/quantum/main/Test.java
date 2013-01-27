package net.bluetempest.quantum.main;

import net.bluetempest.quantum.Psi;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for(int i=0;i<10;i++) {
			Psi register = new Psi(2);
			register.hadamardGate(0);
			register.controlledNot(0,1);
			//System.out.println(arrToString(register.getWeights()));
			
			//register.phaseGate(0, 8);
			//register.phaseGate(1, 8);
			//register.controlledNot(0, 1);
			System.out.println(arrToString(register.collapse()));
		}
		
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
