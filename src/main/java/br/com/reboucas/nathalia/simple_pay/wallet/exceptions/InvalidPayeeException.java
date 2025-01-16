package br.com.reboucas.nathalia.simple_pay.wallet.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class InvalidPayeeException extends SimplePayException {

    public InvalidPayeeException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
