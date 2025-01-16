package br.com.reboucas.nathalia.simple_pay.unit_tests.wallet.services;

import br.com.reboucas.nathalia.simple_pay.authorization.services.AuthorizationService;
import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import br.com.reboucas.nathalia.simple_pay.wallet.WalletType;
import br.com.reboucas.nathalia.simple_pay.wallet.exceptions.*;
import br.com.reboucas.nathalia.simple_pay.wallet.repositories.WalletRepository;
import br.com.reboucas.nathalia.simple_pay.wallet.services.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.COMMON;
import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.SHOPKEEPER;
import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock AuthorizationService authorizationService;
    @InjectMocks WalletService walletService;

    @ParameterizedTest
    @ValueSource(strings = {"COMMON", "SHOPKEEPER"})
    void shouldCreateWallet(String walletTypeName) {
        var walletType = WalletType.valueOf(walletTypeName);
        var wallet = buildWallet(walletType);
        when(walletRepository.existsByCpfCnpjOrEmail(anyString(), anyString())).thenReturn(false);
        when(walletRepository.save(wallet)).thenReturn(getExpectedWallet(walletType));

        var walletCreated = walletService.create(wallet);

        assertNotNull(walletCreated);
        assertNotNull(walletCreated.getId());
        assertEquals(walletType, walletCreated.getWalletType());
        verify(walletRepository, times(1)).save(wallet);
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMMON", "SHOPKEEPER"})
    void shouldThrowExceptionWhenCreateWallet(String walletTypeName) {
        var walletType = WalletType.valueOf(walletTypeName);
        var wallet = buildWallet(walletType);
        when(walletRepository.existsByCpfCnpjOrEmail(anyString(), anyString()))
                .thenReturn(true);

        var exception = assertThrowsExactly(UniqueWalletException.class, () -> walletService.create(wallet));

        assertInstanceOf(UniqueWalletException.class, exception);
        assertEquals("CPF/CNPJ ou email já cadastrados", exception.getMessage());
    }

    @Test
    void shouldFindWalletById() {
        var expectedWallet = getExpectedWallet(COMMON);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(expectedWallet));

        var wallet = walletService.findById(1L);

        assertNotNull(wallet);
        assertEquals(expectedWallet.getId(), wallet.getId());
        verify(walletRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFindWalletById() {
        when(walletRepository.findById(anyLong()))
                .thenThrow(new WalletNotFoundException("Carteira 1 não encontrada"));

        var exception = assertThrowsExactly(WalletNotFoundException.class, () -> walletService.findById(1L));

        assertInstanceOf(WalletNotFoundException.class, exception);
        assertEquals("Carteira 1 não encontrada", exception.getMessage());
        verify(walletRepository, times(1)).findById(1L);
    }

    @Test
    void shouldHaveBalance_9_whenBalanceWallet_10_andDebit_1() {
        var expectedWallet = getExpectedWallet(COMMON);
        expectedWallet.setBalance(TEN);
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(expectedWallet));

        walletService.debit(ONE, 1L);

        assertEquals(new BigDecimal(9), expectedWallet.getBalance());
        verify(walletRepository, times(1)).save(expectedWallet);
    }

    @Test
    void shouldHaveBalance_11_whenBalanceWallet_10_andCredit_1() {
        var expectedWallet = getExpectedWallet(COMMON);
        expectedWallet.setBalance(TEN);
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(expectedWallet));

        walletService.credit(ONE, 1L);

        assertEquals(new BigDecimal(11), expectedWallet.getBalance());
        verify(walletRepository, times(1)).save(expectedWallet);
    }

    @Test
    void shouldTransferSuccessfully() {
        var expectedPayerWallet = getExpectedWallet(COMMON);
        expectedPayerWallet.setBalance(TEN);
        var expectedPayeeWallet = getExpectedWallet(SHOPKEEPER);
        expectedPayeeWallet.setBalance(ZERO);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(expectedPayerWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(expectedPayeeWallet));

        walletService.transfer(1L, 2L, ONE);

        assertEquals(new BigDecimal(9), expectedPayerWallet.getBalance());
        assertEquals(ONE, expectedPayeeWallet.getBalance());
        verify(walletRepository, times(1)).save(expectedPayerWallet);
        verify(walletRepository, times(1)).save(expectedPayeeWallet);
    }

    @ParameterizedTest
    @MethodSource()
    void shouldNotTransferWhenValidationError(Long payerWalletId, Long payeeWalletId, BigDecimal transferValue,
                                              Exception ex) {
        var expectedCommonWallet = getExpectedWallet(COMMON);
        var expectedShopkeeperWallet = getExpectedWallet(SHOPKEEPER);
        when(walletRepository.findById(expectedCommonWallet.getId())).thenReturn(Optional.of(expectedCommonWallet));
        if (!payeeWalletId.equals(payerWalletId))
            when(walletRepository.findById(expectedShopkeeperWallet.getId())).thenReturn(Optional.of(expectedShopkeeperWallet));

        var exception = assertThrowsExactly(ex.getClass(),
                () -> walletService.transfer(payerWalletId, payeeWalletId, transferValue));

        assertInstanceOf(ex.getClass(), exception);
        assertEquals(ex.getMessage(), exception.getMessage());
    }

    static Stream<Arguments> shouldNotTransferWhenValidationError() {
        return Stream.of(
                Arguments.of(1L, 2L, ZERO, new InvalidValueException("Valor inválido")),
                Arguments.of(1L, 2L, new BigDecimal(-1), new InvalidValueException("Valor inválido")),
                Arguments.of(1L, 1L, TEN, new InvalidPayeeException("Beneficiário inválido")),
                Arguments.of(2L, 1L, TEN, new ShopkeeperNotTransferException("Carteira do tipo lojista não pode realizar transferências"))
        );
    }

    private static Wallet buildWallet(WalletType walletType) {
        return Wallet.builder()
                .fullName("Carteira teste")
                .cpfCnpj(walletType.equals(COMMON) ? "12345678900" : "12345678912345")
                .email(String.format("emailteste_%s_@email.com", walletType.name()))
                .password("12345678")
                .walletType(walletType)
                .build();
    }

    private static Wallet getExpectedWallet(WalletType walletType) {
        var wallet = buildWallet(walletType);
        wallet.setId(walletType.equals(COMMON) ? 1L : 2L);
        wallet.setBalance(ZERO);
        return wallet;
    }

}