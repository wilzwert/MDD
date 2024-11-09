package com.openclassrooms.mdd.exception;

import com.openclassrooms.mdd.dto.response.ErrorResponseDto;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Global exception handler to intercept several types of Exceptions
 * and set http response accordingly
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> generateResponseStatusException(ResponseStatusException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        ErrorResponseDto.setMessage(ex.getReason());
        ErrorResponseDto.setStatus(String.valueOf(ex.getStatusCode().value()));
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> generateMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errors = new StringBuilder();

        for(FieldError fieldError : fieldErrors){
            errors.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(". ");
        }
        ErrorResponseDto.setMessage(errors.toString());
        ErrorResponseDto.setStatus(String.valueOf(ex.getStatusCode().value()));
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, ex.getStatusCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> generateAccessDeniedException(AccessDeniedException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        ErrorResponseDto.setStatus(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        ErrorResponseDto.setMessage("Access denied");
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> generateHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        ErrorResponseDto.setStatus(ex.getStatusCode().toString());
        ErrorResponseDto.setMessage("Unsupported method");
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, ex.getStatusCode());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponseDto> generateNumberFormatException(NumberFormatException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        ErrorResponseDto.setStatus(HttpStatus.BAD_REQUEST.toString());
        ErrorResponseDto.setMessage("Bad Request " + ex.getMessage());
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> generateBadRequestException(BadRequestException ex) {
        ErrorResponseDto ErrorResponseDto = new ErrorResponseDto();
        ErrorResponseDto.setStatus(HttpStatus.BAD_REQUEST.toString());
        ErrorResponseDto.setMessage("Bad Request " + ex.getMessage());
        ErrorResponseDto.setTime(new Date().toString());
        return new ResponseEntity<>(ErrorResponseDto, HttpStatus.BAD_REQUEST);
    }
}