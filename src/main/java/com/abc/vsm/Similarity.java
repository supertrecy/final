package com.abc.vsm;

import java.util.List;

public class Similarity {
	public static double docDistance(List<Double> v1, List<Double> v2) {
		int length = v1.size();
		double result = 0.0;
		for (int i = 0; i < length; i++) {
			double num1 = v1.get(i);
			double num2 = v2.get(i);
			if (num1 > 0 && num2 > 0)
				result += num1 * num2;
		}
		return result;
	}

	public static double cosineDistance(List<Double> v1, List<Double> v2) {
		int length = v1.size();
		double numerator = docDistance(v1, v2);
		double denominator = 1.0;
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		for (int i = 0; i < length; i++) {
			double num1 = v1.get(i);
			double num2 = v2.get(i);
			if (num1 == 0)
				tmp1 += num1 * num1;
			if (num2 == 0)
				tmp2 += num2 * num2;
		}
		denominator = Math.sqrt(tmp1 * tmp2);
		return numerator / denominator;
	}
	
	public static double diceDistance(List<Double> v1, List<Double> v2) {
		int length = v1.size();
		double numerator = 2*docDistance(v1, v2);
		double denominator = 0.0;
		for (int i = 0; i < length; i++) {
			double num1 = v1.get(i);
			double num2 = v2.get(i);
			if (num1 == 0)
				denominator += num1 * num1;
			if (num2 == 0)
				denominator += num2 * num2;
		}
		return numerator / denominator;
	}
	
	public static double jaccardDistance(List<Double> v1, List<Double> v2) {
		int length = v1.size();
		double numerator = docDistance(v1, v2);
		double denominator = 0.0;
		for (int i = 0; i < length; i++) {
			double num1 = v1.get(i);
			double num2 = v2.get(i);
			if (num1 == 0)
				denominator += num1 * num1;
			if (num2 == 0)
				denominator += num2 * num2;
		}
		denominator -= numerator;
		return numerator / denominator;
	}
}
