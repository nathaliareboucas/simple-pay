package br.com.reboucas.nathalia.simple_pay.transfer.controllers.v1.dtos;

import br.com.reboucas.nathalia.simple_pay.transfer.Transfer;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferDTO(
        @NotNull BigDecimal value,
        @NotNull Long payer,
        @NotNull Long payee) {

    public Transfer toTransfer() {
        return Transfer.builder()
                .value(value)
                .payer(payer)
                .payee(payee)
                .build();
    }

    public static TransferDTO build(Transfer transfer) {
        return new TransferDTO(
                transfer.getValue(),
                transfer.getPayer(),
                transfer.getPayee()
        );
    }
}
