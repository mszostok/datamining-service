package com.mszostok.web.rest;

import com.mszostok.exception.CompetitionException;
import com.mszostok.exception.DescriptionException;
import com.mszostok.exception.InternalException;
import com.mszostok.exception.SubmissionException;
import com.mszostok.exception.UploadException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.ui.ModelMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

@ControllerAdvice
public class HandleExceptionAdvice {

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  ModelMap handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    ModelMap errorMap = new ModelMap();
    for (FieldError fieldError : errors) {
      errorMap.addAttribute(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return errorMap;
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler({CompetitionException.class, UploadException.class,
    DescriptionException.class, SubmissionException.class})
  @ResponseBody
  ModelMap handleCompetitionException(final RuntimeException ex) {
    return new ModelMap().addAttribute("error", ex.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(InternalException.class)
  @ResponseBody
  ModelMap handleCustomInternalException(final InternalException ex) {
    return new ModelMap().addAttribute("error", ex.getMessage());
  }

  @ResponseStatus(PAYLOAD_TOO_LARGE)
  @ExceptionHandler({MultipartException.class, FileUploadBase.FileSizeLimitExceededException.class})
  @ResponseBody
  ModelMap handleSizeLimitExceededException(final MultipartException ex) {
    return new ModelMap().addAttribute("error", "Was exceeded file size limit");
  }
}
