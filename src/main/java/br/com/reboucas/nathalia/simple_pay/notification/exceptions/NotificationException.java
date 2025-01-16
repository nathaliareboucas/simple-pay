package br.com.reboucas.nathalia.simple_pay.notification.exceptions;

import br.com.reboucas.nathalia.simple_pay.handler.SimplePayException;
import org.springframework.http.HttpStatus;

public class NotificationException extends SimplePayException {

    public NotificationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
