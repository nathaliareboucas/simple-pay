package br.com.reboucas.nathalia.simple_pay.wallet.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class InvalidValueException extends SimplePayException {

    public InvalidValueException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
