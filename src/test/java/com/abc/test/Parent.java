package com.abc.test;

public class Parent{
	protected String name;
	public String a;
	
	protected void print() {
		System.out.println(name);
		System.out.println(a);
	}
	
	public static void main(String[] args) {
		Parent p = new Sub();
		p.print();
	}
}
class Sub extends Parent{
	protected String name = "sub";
	public String a = "suba";
	
	protected void print() {
		System.out.println(name);
		System.out.println(super.a);
	}
}
