package com.legou.common.advice;

import com.legou.common.exception.LgException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionAdvice {
    @ExceptionHandler(LgException.class)
    public ResponseEntity<String> handleLyException(LgException e){
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}
