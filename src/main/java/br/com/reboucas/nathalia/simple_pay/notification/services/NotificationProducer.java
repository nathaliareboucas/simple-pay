package br.com.reboucas.nathalia.simple_pay.notification.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, TransactionDTO> kafkaTemplate;
    private static final String TRANSFER_NOTIFICATION_TOPIC = "transfer-notification";

    public void sendNotification(Long payer, Long payee, BigDecimal value) {
        var transactionDTO = new TransactionDTO(payer, payee, value);
        kafkaTemplate.send(TRANSFER_NOTIFICATION_TOPIC, transactionDTO);
    }
}
