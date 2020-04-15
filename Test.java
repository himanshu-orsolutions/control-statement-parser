package com.parse.samples;

public class Test {
  public static void main(String[] args) {
    int i = 0;
    boolean P_0 = i < 2;
    while (P_0) {
      System.out.println("I am inside for loop with braces");
      int j = 0;
      boolean P_1 = j < i;
      while (P_1) {
        System.out.println("I am inside for loop without braces");
        j++;
        P_1 = j < i;
      }
      int a = 2;
      boolean P_2 = a-- > 0;
      while (P_2) {
        System.out.println("I am inside while loop without braces");
        P_2 = a-- > 0;
      }
      int b = 2;
      boolean P_3 = b-- > 0;
      while (P_3) {
        System.out.println("I am inside while loop with braces.");
        P_3 = b-- > 0;
      }
      int c = 2;
      boolean P_4;
      do {
        System.out.println("I am inside do while loop");
        P_4 = c-- > 0;
      } while (P_4);
      int e = 2;
      int f = 2;
      int d = 0;
      boolean P_5 = d < 2;
      while (P_5) {
        boolean P_6 = e-- > 0;
        while (P_6) {
          boolean P_7;
          do {
            System.out.println("Test X");
            P_7 = f-- > 0;
          } while (P_7);
          P_6 = e-- > 0;
        }
        d++;
        P_5 = d < 2;
      }
      i++;
      P_0 = i < 2;
    }
  }
}
