package com.mszostok.utils;

import java.util.List;

@FunctionalInterface
public interface Command {
  Double apply(List<String[]> submission, List<String[]> key, Object... args);
}
