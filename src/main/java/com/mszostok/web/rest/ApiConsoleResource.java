package com.mszostok.web.rest;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api-console")
public class ApiConsoleResource {

  private static final String SWAGGER_URL = "index.html";

  @GetMapping
  public String apiConsole() {
    return "redirect:/" + SWAGGER_URL;
  }
}
