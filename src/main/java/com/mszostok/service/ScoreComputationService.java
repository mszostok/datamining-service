package com.mszostok.service;

import com.mszostok.exception.IllegalArgException;
import com.mszostok.utils.Command;
import com.mszostok.utils.ScoreComputation;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ScoreComputationService {

  private static final Map<ScoreFunctionType, Command> SCORE_FUNCTIONS = new HashMap<>();

  static {
    SCORE_FUNCTIONS.put(ScoreFunctionType.MATCH_ALL, ScoreComputation::matchAll);
  }

  public static SubmissionBuilder scoreFor() {
    return submission -> () -> key -> (metric, args) -> SCORE_FUNCTIONS.get(metric).apply(submission, key, args);
  }

  @Getter
  public enum ScoreFunctionType {
    MATCH_ALL("Match All", 1);

    private String name;
    private Integer id;

    ScoreFunctionType(final String name, final Integer id) {
      this.name = name;
      this.id = id;
      Holder.MAP.put(id, this);
    }

    public static ScoreFunctionType find(final Integer id) {
      ScoreFunctionType sft = Holder.MAP.get(id);
      if (isNull(sft)) {
        throw new IllegalArgException(String.format("Unsupported type %d.", id));
      }
      return sft;
    }

    private static class Holder {
      static final Map<Integer, ScoreFunctionType> MAP = new HashMap<>();
    }

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
    Double withMetric(ScoreFunctionType metric, Object... args);
  }
}
