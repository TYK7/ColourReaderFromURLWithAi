package com.example.colorschemeidentifier.web;

import com.example.colorschemeidentifier.model.ColorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Bad Request: {} (URL: {})", ex.getMessage(), request.getDescription(false));
        // Return a list of ColorInfo with the error, matching what the frontend might expect for some errors.
        // Alternatively, return a dedicated error DTO. For consistency with some service error paths:
        ColorInfo errorInfo = new ColorInfo(null, null, ex.getMessage(), "input_validation");
        return new ResponseEntity<>(List.of(errorInfo), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Internal Server Error: {} (URL: {})", ex.getMessage(), request.getDescription(false), ex);
        ColorInfo errorInfo = new ColorInfo(null, null, "An unexpected server error occurred.", "server_error");
        return new ResponseEntity<>(List.of(errorInfo), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
