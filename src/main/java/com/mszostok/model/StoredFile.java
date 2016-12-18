package com.mszostok.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredFile {
  private String filename;
  private String refLink;

  public StoredFile(final String originalFilename, final String link) {
    this.filename = originalFilename;
    this.refLink = link;
  }
}
