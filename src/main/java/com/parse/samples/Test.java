package com.parse.samples;

public class Test {

  public static void main(String[] args) {
    int i = 0;
    boolean P_0 = Integer.valueOf(i).equals(0);
    boolean P_1 = Integer.valueOf(i).equals(1);
    if (P_0) {
      System.out.println("i is 0");
    } else if (P_1) {
      System.out.println("i is 1");
      System.out.println("i is greater than 1");
    } else {
      System.out.println("i is greater than 1");
    }
  }
}
