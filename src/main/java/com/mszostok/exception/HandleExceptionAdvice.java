package com.mszostok.exception;

import org.springframework.ui.ModelMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class HandleExceptionAdvice {

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ModelMap handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    ModelMap errorMap = new ModelMap();
    for (FieldError fieldError : errors) {
      errorMap.addAttribute(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return errorMap;
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler({CompetitionException.class, UploadException.class, DescriptionException.class})
  public ModelMap handleCompetitionException(final CompetitionException ex) {
    return new ModelMap().addAttribute("error", ex.getMessage());
  }
}
