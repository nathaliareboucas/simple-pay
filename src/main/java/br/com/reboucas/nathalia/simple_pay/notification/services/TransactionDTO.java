package br.com.reboucas.nathalia.simple_pay.notification.services;

import java.math.BigDecimal;

public record TransactionDTO(
        Long payer,
        Long payee,
        BigDecimal value) {
}
