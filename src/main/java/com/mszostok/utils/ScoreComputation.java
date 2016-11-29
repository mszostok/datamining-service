package com.mszostok.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreComputation {

  public static Double matchAll(final List<String[]> keyList, final List<String[]> submissionList, final Object... properties) {
    final AtomicInteger idx = new AtomicInteger();
    final AtomicInteger points = new AtomicInteger();

    keyList.forEach(line -> {
      if (line[0].equals(submissionList.get(idx.getAndIncrement())[0])) {
        points.incrementAndGet();
      }
    });
    return points.doubleValue();
  }
}
