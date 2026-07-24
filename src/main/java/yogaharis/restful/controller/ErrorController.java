package yogaharis.restful.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.model.WebResponse;

import java.util.stream.Collectors;


@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception){
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        WebResponse<String> build = WebResponse.<String>builder().errors(message).build();
        return ResponseEntity.badRequest().body(build);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> responseStatusException(ResponseStatusException exception){
        WebResponse<String> build = WebResponse.<String>builder().errors(exception.getReason()).build();
        return ResponseEntity.status(exception.getStatusCode()).body(build);
    }
}
