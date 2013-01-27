package net.bluetempest.math;

public class Complex implements Cloneable{
	float r = 0; // Real part
	float i = 0; // imaginary part
	
	public static final float E = (float) Math.E;
	public static final float PI = (float) Math.PI;
	
	public Complex() {
		this.r = 0;
		this.i = 0;
	}

	public Complex(float re, float im) {
		this.r = re;
		this.i = im;
	}

	public Complex(Complex input) {
		this.r = input.getReal();
		this.i = input.getImaginary();
	}

	public float getReal() {
		return r;
	}
	public void setReal(float r) {
		this.r = r;
	}
	public float getImaginary() {
		return i;
	}
	public void setImaginary(float i) {
		this.i = i;
	}

	public Complex getConjugate() {
		return new Complex(this.r, this.i * (-1));
	}
	
	public Complex add(Complex op) {
		Complex result = new Complex();
		result.setReal(this.r + op.getReal());
		result.setImaginary(this.i + op.getImaginary());
		return result;
	}

	public Complex sub(Complex op) {
		Complex result = new Complex();
		result.setReal(this.r - op.getReal());
		result.setImaginary(this.i - op.getImaginary());
		return result;
	}

	public Complex mul(Complex op) {
		Complex result = new Complex();
		result.setReal(this.r * op.getReal() - this.i * op.getImaginary());
		result.setImaginary(this.r * op.getImaginary() + this.i * op.getReal());
		return result;
	}

	public Complex div(Complex op) {
		Complex result = new Complex(this);
		result = result.mul(op.getConjugate());
		float opNormSq = op.getReal() * op.getReal() + op.getImaginary() * op.getImaginary();
		result.setReal(result.getReal() / opNormSq);
		result.setImaginary(result.getImaginary() / opNormSq);
		return result;
	}
	
	public Complex exp() {
		float expr = (float) Math.exp(this.r);
		float sini = (float) Math.sin(this.i);
		float cosi = (float) Math.cos(this.i);
		
		return new Complex(expr * cosi, expr*sini);
	}
	
	public String toString() {
		if (this.r == 0) {
			if (this.i == 0) {
				return "0";
			} else {
				return (this.i + "i");
			}
		} else {
			if (this.i == 0) {
				return String.valueOf(this.r);
			} else if (this.i < 0) {
				return (this.r + " " + this.i + "i");
			} else {
				return (this.r + " +" + this.i + "i");
			}
		}
	}
	
	public Complex abs() {
		return new Complex((float) Math.sqrt(r*r + i*i), 0);
	}
	
	public Complex clone() {
		return new Complex(r,i);
	}
}
