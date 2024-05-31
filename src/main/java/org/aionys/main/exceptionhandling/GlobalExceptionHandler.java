package org.aionys.main.exceptionhandling;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@RestControllerAdvice("org.aionys.main")
@Slf4j
public class GlobalExceptionHandler {

    // This method is used to handle the exception when a unique constraint is violated
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<FieldError> handleDataIntegrityViolationException(
            org.hibernate.exception.ConstraintViolationException e
    ) {
        log.warn("Data integrity violation: {}", e.getMessage());
        if (e.getMessage().contains(" unique ")) {
            var pattern = Pattern.compile("\\(\\w+\\)");
            var matcher = pattern.matcher(e.getMessage());
            if (matcher.find()) {
                var field = matcher.group().substring(1, matcher.group().length() - 1);
                if (matcher.find()) {
                    var value = matcher.group().substring(1, matcher.group().length() - 1);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            new FieldError("Object with %s=%s already exists".formatted(field, value), field, value)
                    );
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    // TODO: Replace parameter name with underscore when upgraded to Java 22 or above
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<FieldError>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
                e.getConstraintViolations().stream().map(
                        violation -> new FieldError(
                                violation.getMessage(),
                                violation.getPropertyPath().toString(),
                                violation.getInvalidValue().toString()
                        )
                ).toList()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldError>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Method arguments validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
                e.getBindingResult().getFieldErrors().stream().map(
                        error -> new FieldError(
                                error.getDefaultMessage(),
                                error.getField(),
                                Objects.toString(error.getRejectedValue())
                        )
                ).toList()
        );
    }
}
