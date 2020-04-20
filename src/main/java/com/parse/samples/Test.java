package com.parse.samples;

public class Test {

	public static int func(String... arr) {

		return 1;
	}

	public static void main(String[] args) {

		int a = 3;
		switch (a) {
		case 1:
			System.out.println("Its 1");
			break;
		case 2:
			System.out.println("Its 2");
			break;
		case 3:
			System.out.println("Its 3");

			for (int i = 0; i < 5; i++) {
				System.out.println("Cool");
			}
		case 4:
			System.out.println("Its 4");
			break;
		default:
			System.out.println("Its default");
		}
	}
}
