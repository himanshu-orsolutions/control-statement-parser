package com.parse.samples;

public class Test {
	public static void main(String[] args) {

		int[][] a = new int[2][2];
		try {
			try {
				System.out.println(a[3][0]);
			} catch (Exception arrayIndexOutOfBoundsException) {

			}
		} catch (IllegalArgumentException arrayIndexOutOfBoundsException) {

		}
	}
}
