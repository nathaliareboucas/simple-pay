package br.com.reboucas.nathalia.simple_pay.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class SimplePayExceptionHandler {

    @ExceptionHandler(SimplePayException.class)
    public ResponseEntity<ProblemDetail> handleSimplePayException(SimplePayException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getProblemDetail(), HttpStatus.valueOf(ex.getProblemDetail().getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));

        var problemDetail = ex.getBody();
        problemDetail.setTitle("SimplePay Exception");
        problemDetail.setDetail("Conteúdo da requisição inválido");
        problemDetail.setProperties(fieldErrors);

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }
}
