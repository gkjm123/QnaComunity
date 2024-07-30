package com.example.qnacomunity.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> customExceptionHandler(final CustomException e) {
    log.warn("커스텀 에러 발생: {}", e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(FormException.class)
  public ResponseEntity<String> formExceptionHandler(final FormException e) {
    log.warn("양식 에러 발생: {}", e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
