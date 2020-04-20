package com.parse.samples;

public class Test {

	public static void main(String[] args) {

		// Simple for loop
		for (int i = 0; i < 2; i++) {
			System.out.println(i);
		}

		// Simple while loop
		int i = 0;
		while (i++ < 2) {
			System.out.println(i);
		}

		// Simple do-while loop
		i = 0;
		do {
			System.out.println(i);
		} while (i++ < 2);

		// Simple if condition
		i = 2;
		if (i < 5) {
			System.out.println("i is less than 5");
		}

		// Simple if-else condition
		i = 6;
		if (i < 5) {
			System.out.println("i is less than 5");
		} else {
			System.out.println("i is greater than 5");
		}

		// Simple if-elseif-else condition
		i = 10;
		if (i > 0 && i < 5) {
			System.out.println("i is in range 1-4");
		} else if (i >= 5 && i < 10) {
			System.out.println("i is in range 5-9");
		} else {
			System.out.println("i is greater than 9");
		}

		// Simple switch
		i = 0;
		switch (i) {
		case 0:
			System.out.println("i is 0");
			break;
		case 1:
			System.out.println("i is 1");
			break;
		default:
			System.out.println("i is greater than 1");
		}

		// Simple ternary
		i = 10;
		i = i > 5 ? i - 2 : i + 2;
		System.out.println(i);
	}
}
