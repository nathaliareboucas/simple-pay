package br.com.reboucas.nathalia.simple_pay.unit_tests.transfer.services;


import br.com.reboucas.nathalia.simple_pay.notification.services.NotificationProducer;
import br.com.reboucas.nathalia.simple_pay.transfer.Transfer;
import br.com.reboucas.nathalia.simple_pay.transfer.repositories.TransferRepository;
import br.com.reboucas.nathalia.simple_pay.transfer.services.TransferService;
import br.com.reboucas.nathalia.simple_pay.wallet.services.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime
            .of(2025, 1, 8, 22, 10, 10);

    @Mock TransferRepository transferRepository;
    @Mock WalletService walletService;
    @Mock NotificationProducer notificationProducer;
    @InjectMocks TransferService transferService;

    @Test
    void shouldTransferSuccessfully() {
        var transfer = buildTransfer();
        var expectedTransfer = getExpectedTransfer();
        when(transferRepository.save(any(Transfer.class))).thenReturn(expectedTransfer);

        var createdTransfer = transferService.create(transfer);

        assertNotNull(createdTransfer);
        assertNotNull(createdTransfer.getId());
        assertEquals(CREATED_AT, expectedTransfer.getCreatedAt());
        verify(walletService, times(1))
                .transfer(transfer.getPayer(), transfer.getPayee(), transfer.getValue());
        verify(transferRepository, times(1)).save(transfer);
        verify(notificationProducer, times(1))
                .sendNotification(transfer.getPayer(), transfer.getPayee(), transfer.getValue());
    }

    private static Transfer buildTransfer() {
        return Transfer.builder()
                .payer(1L)
                .payee(2L)
                .value(TEN)
                .createdAt(CREATED_AT)
                .build();
    }

    private static Transfer getExpectedTransfer() {
        var transfer = buildTransfer();
        transfer.setId(1L);
        return transfer;
    }

}