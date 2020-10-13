package com.fpt.gta.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${config.debug}")
    private boolean debug;

//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity handleException(RuntimeException ex, WebRequest request) {
//        if (debug) {
//            ex.printStackTrace();
//            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        } else {
//            return new ResponseEntity("Exception From Server", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity handleNoSuchElementException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
            return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity("NoSuchElementException: No value present", HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity handleNotFoundException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity handleConflictException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity handleForbiddenException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    protected ResponseEntity internalServerErrorException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NumberFormatException.class)
    protected ResponseEntity numberFormatException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    protected ResponseEntity UnprocessableEntityException(RuntimeException ex, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (debug) {
            ex.printStackTrace();
        }
        if (ex.getMessage().contains("Missing request attribute 'FirebaseToken'")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return super.handleServletRequestBindingException(ex, headers, status, request);
        }
    }
}