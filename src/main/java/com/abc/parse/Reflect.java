package com.abc.parse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflect {
	public static void printMethods(String classname) throws ClassNotFoundException{
			Class cl= Class.forName(classname);
			Method[] methods = cl.getMethods();
			for (Method method : methods) {
				System.out.println(Modifier.toString(method.getModifiers())+" "+method.getReturnType().getName()+" "+method.getName());
			}
	}
	
	public static void printDeclaredMethods(String classname) throws ClassNotFoundException{
			Class cl= Class.forName(classname);
			Method[] methods = cl.getDeclaredMethods();
			for (Method method : methods) {
				System.out.println(Modifier.toString(method.getModifiers())+" "+method.getReturnType().getName()+" "+method.getName());
			}
	}
	
	public static void printDeclaredField(String classname) throws ClassNotFoundException{
			Class cl= Class.forName(classname);
			Field[] fields = cl.getDeclaredFields();
			for (Field field : fields) {
				System.out.println(Modifier.toString(field.getModifiers())+" "+field.getName());
			}
	}
	
	public static void printField(String classname) throws ClassNotFoundException{
			Class cl= Class.forName(classname);
			Field[] fields = cl.getFields();
			for (Field field : fields) {
				System.out.println(Modifier.toString(field.getModifiers())+" "+field.getName());
			}
	}
	
	public static void main(String[] args) {
		try {
			printField("com.abc.parse.QQParser");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
