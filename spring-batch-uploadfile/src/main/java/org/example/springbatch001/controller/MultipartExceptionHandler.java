package org.example.springbatch001.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MultipartExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Invalid multipart request. Send the CSV using form-data with key 'file'. "
                        + "Do not manually set Content-Type; include a generated multipart boundary.");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<String> handleMissingFile(MissingServletRequestPartException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Missing CSV file. Add a form-data part with key 'file'.");
    }
}
