package com.parse.samples;

public class Test {
  public static void main(String[] args) {
    int a = 10;
    boolean P_1 = a < 20;
    boolean P_2 = a < 20;
    boolean P_0 = a < 10;
    if (P_0) {
      System.out.println("if");
    } else if (P_1) {
      System.out.println("else if");
    } else if (P_2) {
      System.out.println("else if");
    } else {
      System.out.println("else");
    }
    int i = 0;
    boolean P_3 = i < 10;
    while (P_3) {
      System.out.println("cool");
      i++;
      P_3 = i < 10;
    }
  }
}
