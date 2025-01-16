package br.com.reboucas.nathalia.simple_pay.wallet.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends SimplePayException {

    public WalletNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
