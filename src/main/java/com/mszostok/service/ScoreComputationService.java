package com.mszostok.service;

import com.mszostok.utils.Command;
import com.mszostok.utils.ScoreComputation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreComputationService {

  private static final Map<String, Command> COMPUTE_FUNCTIONS = new HashMap<>();

  static {
    COMPUTE_FUNCTIONS.put("checkAll", ScoreComputation::checkAll);
  }

  public static SubmissionBuilder scoreFor() {
    return submission -> () -> key -> (metric, args) -> COMPUTE_FUNCTIONS.get(metric).apply(submission, key, args);
  }

  interface SubmissionBuilder {
    AndBuilder submission(List<String[]> submission);
  }

  interface KeyBuilder {
    MetricBuilder key(List<String[]> key);
  }

  interface AndBuilder {
    KeyBuilder and();
  }

  interface MetricBuilder {
    Double withMetric(String metric, Object... args);
  }
}
