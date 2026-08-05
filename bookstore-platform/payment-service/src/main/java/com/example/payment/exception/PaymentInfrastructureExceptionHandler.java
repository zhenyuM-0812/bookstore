package com.example.payment.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class PaymentInfrastructureExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex,
            HttpServletRequest request) {

        HttpStatus status;
        String message;

        switch (ex.status()) {
            case 400 -> {
                status = HttpStatus.BAD_REQUEST;
                message = "Order service rejected the request.";
            }
            case 401 -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "Authentication was rejected by order service.";
            }
            case 403 -> {
                status = HttpStatus.FORBIDDEN;
                message = "You do not have permission to access this order.";
            }
            case 404 -> {
                status = HttpStatus.NOT_FOUND;
                message = "The requested order does not exist.";
            }
            case 409 -> {
                status = HttpStatus.CONFLICT;
                message = "The order is not in a valid state for this operation.";
            }
            default -> {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                message = "Order service is temporarily unavailable. Please try again later.";
            }
        }

        return buildResponse(
                status,
                message,
                request
        );
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "A payment for this order already exists or violates a database constraint.",
                request
        );
    }


    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .validationErrors(null)
                .build();

        return new ResponseEntity<>(
                error,
                status
        );
    }
}
