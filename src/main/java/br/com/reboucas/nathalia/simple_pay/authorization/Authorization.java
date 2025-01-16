package br.com.reboucas.nathalia.simple_pay.authorization;

import lombok.Data;

@Data
public class Authorization {
    private String status;
    private AuthorizationData data;
}
