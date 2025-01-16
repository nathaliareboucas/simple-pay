package br.com.reboucas.nathalia.simple_pay.wallet.controllers.v1.dtos;

import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import br.com.reboucas.nathalia.simple_pay.wallet.WalletType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WalletDTO (
        @NotBlank String fullName,
        @NotBlank String cpfCnpj,
        @NotBlank String email,
        @NotBlank String password,
        BigDecimal balance,
        @NotNull WalletType walletType){

    public Wallet toWallet() {
        return Wallet.builder()
                .fullName(fullName)
                .cpfCnpj(cpfCnpj)
                .email(email)
                .password(password)
                .balance(balance == null ? BigDecimal.ZERO : balance)
                .walletType(walletType)
                .build();
    }

    public static WalletDTO build(Wallet wallet) {
        return new WalletDTO(
                wallet.getFullName(),
                wallet.getCpfCnpj(),
                wallet.getEmail(),
                wallet.getPassword(),
                wallet.getBalance(),
                wallet.getWalletType());
    }
}
