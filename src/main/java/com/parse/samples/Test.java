package com.parse.samples;

public class Test {
  public static void main(String[] args) {

    int i = 0;
    boolean P_0 = true;
    boolean START_0 = true;
    while (true) {
      if (START_0) {
        START_0 = false;
      } else {
        P_0 = i++ < 10;
      }
      if (P_0) {
        System.out.println("cool");
      } else {
        break;
      }
    }
  }
}
