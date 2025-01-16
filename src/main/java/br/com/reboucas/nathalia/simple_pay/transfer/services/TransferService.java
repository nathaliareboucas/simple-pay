package br.com.reboucas.nathalia.simple_pay.transfer.services;

import br.com.reboucas.nathalia.simple_pay.notification.services.NotificationProducer;
import br.com.reboucas.nathalia.simple_pay.transfer.Transfer;
import br.com.reboucas.nathalia.simple_pay.transfer.repositories.TransferRepository;
import br.com.reboucas.nathalia.simple_pay.wallet.services.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final TransferRepository transferRepository;
    private final WalletService walletService;
    private final NotificationProducer notificationProducer;

    @Transactional
    public Transfer create(Transfer transfer) {
        walletService.transfer(transfer.getPayer(), transfer.getPayee(), transfer.getValue());
        transfer.setCreatedAt(LocalDateTime.now());
        var transferSaved = transferRepository.save(transfer);
        log.info("Transferência realizada com sucesso - transferenciaId: {}", transferSaved.getId());

        notificationProducer.sendNotification(transfer.getPayer(), transfer.getPayee(), transfer.getValue());
        return transferSaved;
    }
}
