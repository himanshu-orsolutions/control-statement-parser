package com.parse.samples;

public class Test {

	public static void main(String[] args) {

		for (int i = 0; i < 2; i++) {
			System.out.println("I am inside for loop with braces");
			for (int j = 0; j < i; j++)
				System.out.println("I am inside for loop without braces");

			int a = 2;
			while (a-- > 0)
				System.out.println("I am inside while loop without braces");

			int b = 2;
			while (b-- > 0) {
				System.out.println("I am inside while loop with braces.");
			}

			int c = 2;
			do {
				System.out.println("I am inside do while loop");
			} while (c-- > 0);

			int e = 2;
			int f = 2;
			for (int d = 0; d < 2; d++)
				while (e-- > 0)
					do {
						System.out.println("Test X");
					} while (f-- > 0);
		}
	}
}
