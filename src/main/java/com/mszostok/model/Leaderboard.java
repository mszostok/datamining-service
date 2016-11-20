package com.mszostok.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Leaderboard {
  private HashMap<Integer, String> members;
}
