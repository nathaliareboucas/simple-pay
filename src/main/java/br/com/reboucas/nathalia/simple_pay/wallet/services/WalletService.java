package br.com.reboucas.nathalia.simple_pay.wallet.services;

import br.com.reboucas.nathalia.simple_pay.authorization.services.AuthorizationService;
import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import br.com.reboucas.nathalia.simple_pay.wallet.exceptions.*;
import br.com.reboucas.nathalia.simple_pay.wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final AuthorizationService authorizationService;

    public Wallet create(Wallet wallet) {
        boolean exists = walletRepository.existsByCpfCnpjOrEmail(wallet.getCpfCnpj(), wallet.getEmail());
        if (exists)
            throw new UniqueWalletException("CPF/CNPJ ou email já cadastrados");

        var walletCreated = walletRepository.save(wallet);
        log.info("Carteira criada - walletId: {}", walletCreated.getId());
        return walletCreated;
    }

    public Wallet findById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(format("Carteira %s não encontrada", walletId)));
    }

    public void debit(BigDecimal debitValue, Long payerWalletId) {
        var payer = findById(payerWalletId);
        payer.debit(debitValue);
        walletRepository.save(payer);
        log.info("Débito de R${} na carteira {}", debitValue, payer.getId());
    }

    public void credit(BigDecimal creditValue, Long payeeWalletId) {
        var payee = findById(payeeWalletId);
        payee.credit(creditValue);
        walletRepository.save(payee);
        log.info("Crédito de R${} na carteira {}", creditValue, payee.getId());
    }

    @Transactional
    public void transfer(Long payerId, Long payeeId, BigDecimal transferValue) {
        var payer = findById(payerId);
        var payee = findById(payeeId);
        validate(payer, payee, transferValue);
        debit(transferValue, payer.getId());
        credit(transferValue, payee.getId());
        authorizationService.authorize(payer.getId(), payee.getId(), transferValue);
        log.info("Realizada a transferência de R${} da carteira {} para carteira {}", transferValue, payerId, payeeId);
    }

    private void validate(Wallet payer, Wallet payee, BigDecimal transferValue) {
        var lessThanZero = transferValue.compareTo(ZERO) < 0;

        if (transferValue.equals(ZERO) || lessThanZero)
            throw new InvalidValueException("Valor inválido");

        if (payer.equals(payee))
            throw new InvalidPayeeException("Beneficiário inválido");

        if (payer.isShopkeeper())
            throw new ShopkeeperNotTransferException("Carteira do tipo lojista não pode realizar transferências");
    }
}
