package com.mszostok.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TokenMap implements Serializable {
  private static final long serialVersionUID = 1783301841165570906L;

  private String token;
  private String refreshToken;
}
