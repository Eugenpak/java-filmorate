package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.*;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler(ParameterNotValidException.class)
    public ResponseEntity<ErrorMessage> handleParameterNotValidException(ParameterNotValidException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "ParameterNotValidException",e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "ValidationException",e.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ReviewValidException.class)
    public ResponseEntity<ErrorMessage> handleReviewValidException(ReviewValidException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "ReviewValidException",e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.NOT_FOUND.value(), "NotFoundException", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "MethodArgumentNotValidException", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleUnknownException(Throwable e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Throwable", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}