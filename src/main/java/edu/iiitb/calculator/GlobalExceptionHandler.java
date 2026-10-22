package edu.iiitb.calculator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DivideByZeroException.class)
    public ResponseEntity<ApiError> handleDivideByZero(DivideByZeroException ex, HttpServletRequest req) {
        return badRequest(ex.getMessage(), req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return badRequest("missing required parameter '" + ex.getParameterName() + "'", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return badRequest("parameter '" + ex.getName() + "' must be a number", req);
    }

    private ResponseEntity<ApiError> badRequest(String message, HttpServletRequest req) {
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), message, req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }
}
