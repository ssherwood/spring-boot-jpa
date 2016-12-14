package com.undertree.symptom.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public void handleException(MethodArgumentNotValidException ex,
                                WebRequest request, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getModel().toString());
    }
    /*
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                     WebRequest request, HttpServletResponse response) {
        // TODO needs work to figure out how to better map this for output
        int i = 0;
        Map<String, String> errorBlock = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errorBlock.put(error.getCode() + i++, error.toString());
        }

        return new ResponseEntity<>(errorBlock, HttpStatus.BAD_REQUEST);
    }
    */
}
