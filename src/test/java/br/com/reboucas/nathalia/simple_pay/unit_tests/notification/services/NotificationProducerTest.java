package br.com.reboucas.nathalia.simple_pay.unit_tests.notification.services;

import br.com.reboucas.nathalia.simple_pay.notification.services.NotificationProducer;
import br.com.reboucas.nathalia.simple_pay.notification.services.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static java.math.BigDecimal.TEN;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationProducerTest {
    private static final String TRANSFER_NOTIFICATION_TOPIC = "transfer-notification";

    @Mock
    KafkaTemplate<String, TransactionDTO> kafkaTemplate;

    @InjectMocks
    NotificationProducer notificationProducer;

    @Test
    void shouldSendNotification() {
        var transferDTO = new TransactionDTO(1L, 2L, TEN);
        notificationProducer.sendNotification(1L, 2L, TEN);
        verify(kafkaTemplate, times(1)).send(TRANSFER_NOTIFICATION_TOPIC, transferDTO);
    }

}