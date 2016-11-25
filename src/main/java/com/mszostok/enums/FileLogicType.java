package com.mszostok.enums;

import lombok.Getter;

@Getter
public enum FileLogicType {
  KEY("key"), TRAINING("training"), TESTING("testing");

  private String name;

  FileLogicType(final String name) {
    this.name = name;
  }

}
