package com.mszostok.utils;

public class Checker {

  public static boolean isNotNull(final Object... objs) {
    for (Object obj : objs) {
      if (obj == null)
        return false;
    }
    return true;
  }
}
