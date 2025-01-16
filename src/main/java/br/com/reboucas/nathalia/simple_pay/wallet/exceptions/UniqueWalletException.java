package br.com.reboucas.nathalia.simple_pay.wallet.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class UniqueWalletException extends SimplePayException {

    public UniqueWalletException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
