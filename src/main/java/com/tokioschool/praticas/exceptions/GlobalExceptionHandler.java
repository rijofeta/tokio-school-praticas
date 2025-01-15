package com.tokioschool.praticas.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView handleProductNotFoundException(ProductNotFoundException exception) {
        String message = messageSourceAccessor.getMessage(
                "exception.product_not_found",
                new Object[]{exception.getProductId()}
        );
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject(
                "errorResponse",
                new ErrorResponse(
                        1404,
                        message,
                        ZonedDateTime.now(ZoneOffset.UTC).toString()
                )
        );
        logger.atWarn()
                .setMessage(message)
                .addKeyValue("status", 1404)
                .log();
        return modelAndView;
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernamealreadyExistsException(
            UsernameAlreadyExistsException exception) {
        String message = messageSourceAccessor.getMessage(
                "exception.username_already_exists",
                new Object[]{exception.getDuplicateUsername()}
        );
        logger.atWarn()
                .setMessage(message)
                .addKeyValue("status", 2400)
                .log();
        return new ResponseEntity<>(
                new ErrorResponse(
                        2400,
                        message,
                        ZonedDateTime.now(ZoneOffset.UTC).toString()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUseException(
            EmailAlreadyInUseException exception) {
        String message = messageSourceAccessor.getMessage(
                "exception.email_already_in_use",
                new Object[]{exception.getDuplicateEmail()}
        );
        logger.atWarn()
                .setMessage(message)
                .addKeyValue("status", 2400)
                .log();
        return new ResponseEntity<>(
                new ErrorResponse(
                        2400,
                        message,
                        ZonedDateTime.now(ZoneOffset.UTC).toString()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException exception) {
        logger.warn("The access token used is expired. exp: {}", exception.getClaims().getExpiration());
        return new ResponseEntity<>(
                new ErrorResponse(
                        2401,
                        "expired_access_token",
                        ZonedDateTime.now(ZoneOffset.UTC).toString()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        logger.atError()
                .setMessage("An exception was thrown, but no specific handler was found. " +
                        "The application might be compromised.")
                .addKeyValue("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setCause(exception)
                .log();
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        messageSourceAccessor.getMessage("error.unexpected"),
                        ZonedDateTime.now(ZoneOffset.UTC).toString()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
