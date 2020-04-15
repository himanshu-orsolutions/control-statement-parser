package com.parse.samples;

public class Test {
  public static void main(String[] args) {
    int i = 0;
    boolean P_0 = i < 10;
    while (P_0) {
      int j = 0;
      boolean P_1 = j < i;
      while (P_1) {
        int k = 0;
        boolean P_2 = k < j;
        while (P_2) {
          System.out.println(String.format("Value of i=%d \n value of j=%d", i, j));
          k++;
          P_2 = k < j;
        }
        j++;
        P_1 = j < i;
      }
      i++;
      P_0 = i < 10;
    }
  }
}
