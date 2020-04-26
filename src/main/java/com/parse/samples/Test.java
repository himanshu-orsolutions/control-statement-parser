package com.parse.samples;

public class Test {
	public static void main(String[] args) {

		for (int i = 0; i < 10; i++) {
			System.out.println("Cool");
		}

		for (int i = 0; i < 10; i++)
			System.out.println("Cool");

		int i = 10;
		if (i < 10) {
			System.out.println("Cool");
		}

		if (i < 10)
			System.out.println("Cool");

		i = 10;
		while (i-- >= 0) {
			System.out.println("cool");
		}

		i = 10;
		while (i-- >= 0)
			System.out.println("cool");

		i = 5;
		do {
			System.out.println("cool");
		} while (i-- >= 0);

		if (i == 5) {
			System.out.println("cool 1");
		} else if (i == 6) {
			System.out.println("cool 2");
		}

		if (i == 5)
			System.out.println("cool 1");
		else if (i == 6)
			System.out.println("cool 2");

		if (i == 5) {
			System.out.println("cool 1");
		} else if (i == 6) {
			System.out.println("cool 2");
		} else {
			System.out.println("cool 3");
		}

		if (i == 5)
			System.out.println("cool 1");
		else if (i == 6)
			System.out.println("cool 2");
		else
			System.out.println("cool 3");

		switch (i) {
		case 1:
			System.out.println("cool1");
			break;
		case 2:
			System.out.println("cool2");
			break;
		default:
			System.out.println("cool3");
		}
	}
}
