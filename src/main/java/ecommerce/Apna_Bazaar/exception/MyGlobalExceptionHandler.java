package ecommerce.Apna_Bazaar.exception;

import ecommerce.Apna_Bazaar.payload.response.ExceptionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // ----------- CUSTOM EXCEPTIONS -----------

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ExceptionResponseDTO> myResourceAlreadyExistException(
            ResourceAlreadyExistException e) {
        ExceptionResponseDTO response = new ExceptionResponseDTO();
        response.setMessage(e.getMessage());
        response.setErrorType("ALREADY_EXISTS");
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotExistException.class)
    public ResponseEntity<ExceptionResponseDTO> myResourceNotExistException(
            ResourceNotExistException e) {
        ExceptionResponseDTO response = new ExceptionResponseDTO();
        response.setMessage(e.getMessage());
        response.setErrorType("RESOURCE_NOT_FOUND");
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBadRequestException(
            BadRequestException e) {
        ExceptionResponseDTO response = new ExceptionResponseDTO();
        response.setMessage(e.getMessage());
        response.setErrorType("BAD_REQUEST");
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
