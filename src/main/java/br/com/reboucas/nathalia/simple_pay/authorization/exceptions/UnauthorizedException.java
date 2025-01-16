package br.com.reboucas.nathalia.simple_pay.authorization.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends SimplePayException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
