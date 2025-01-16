package br.com.reboucas.nathalia.simple_pay.unit_tests.wallet;

import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import br.com.reboucas.nathalia.simple_pay.wallet.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.COMMON;
import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.SHOPKEEPER;
import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void shouldTrueWhenShopkeeperWallet() {
        var wallet = Wallet.builder().walletType(SHOPKEEPER).build();
        boolean isShopkeeper = wallet.isShopkeeper();
        assertTrue(isShopkeeper);
    }

    @Test
    void shouldFalseWhenDontShopkeeperWallet() {
        var wallet = Wallet.builder().walletType(COMMON).build();
        boolean isShopkeeper = wallet.isShopkeeper();
        assertFalse(isShopkeeper);
    }

    @Test
    void shouldHaveBalance_10_whenWalletResetAndCredit_10() {
        var wallet = Wallet.builder().balance(ZERO).build();
        wallet.credit(TEN);
        assertEquals(TEN, wallet.getBalance());
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenDebit() {
        var wallet = Wallet.builder().id(1L).balance(ONE).build();

        var insufficientBalanceException = assertThrowsExactly(InsufficientBalanceException.class,
                () -> wallet.debit(TEN));

        assertInstanceOf(InsufficientBalanceException.class, insufficientBalanceException);
        assertEquals("Carteira 1 com saldo insuficiente", insufficientBalanceException.getMessage());
    }

    @Test
    void shoudHaveBalance_9_whenWallet_10_andDebit_1() {
        var wallet = Wallet.builder().balance(TEN).build();
        wallet.debit(ONE);
        assertEquals(new BigDecimal(9), wallet.getBalance());
    }

}