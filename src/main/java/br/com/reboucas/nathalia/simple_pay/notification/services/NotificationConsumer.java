package br.com.reboucas.nathalia.simple_pay.notification.services;

import br.com.reboucas.nathalia.simple_pay.notification.Notification;
import br.com.reboucas.nathalia.simple_pay.notification.exceptions.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
public class NotificationConsumer {
    private final RestClient restClient;
    private static final String TRANSFER_NOTIFICATION_TOPIC = "transfer-notification";
    private static final String NOTIFICATION_GROUP_ID = "simple-pay-notification";

    public NotificationConsumer(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://util.devi.tools/api/v1/notify").build();
    }

    @KafkaListener(topics = TRANSFER_NOTIFICATION_TOPIC, groupId = NOTIFICATION_GROUP_ID)
    public void receiveNotifiction(TransactionDTO transactionDTO) {
        log.info("Enviando notificação | payer: {} | payee: {} | value: {}",
                transactionDTO.payer(), transactionDTO.payee(), transactionDTO.value());

        var notification = restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new NotificationException("Erro ao enviar notificação");
                }).body(Notification.class);

        if (!isEmpty(notification) && !isEmpty(notification.getData()))
            log.info("Notificação enviada | status: {} | mensagem: {}",
                    notification.getStatus(),
                    notification.getData().getMessage());

    }
}
