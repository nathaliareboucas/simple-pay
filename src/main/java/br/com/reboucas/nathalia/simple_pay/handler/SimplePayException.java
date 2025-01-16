package br.com.reboucas.nathalia.simple_pay.handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

public class SimplePayException extends RuntimeException{
    private final HttpStatusCode httpStatus;
    private final String message;

    public SimplePayException(HttpStatusCode httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ProblemDetail getProblemDetail() {
        var detail = ProblemDetail.forStatusAndDetail(httpStatus, message);
        detail.setTitle("SimplePay Exception");
        return detail;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
