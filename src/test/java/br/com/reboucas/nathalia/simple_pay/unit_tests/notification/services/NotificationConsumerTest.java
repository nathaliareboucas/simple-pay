package br.com.reboucas.nathalia.simple_pay.unit_tests.notification.services;

import br.com.reboucas.nathalia.simple_pay.notification.Notification;
import br.com.reboucas.nathalia.simple_pay.notification.NotificationData;
import br.com.reboucas.nathalia.simple_pay.notification.exceptions.NotificationException;
import br.com.reboucas.nathalia.simple_pay.notification.services.NotificationConsumer;
import br.com.reboucas.nathalia.simple_pay.notification.services.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(NotificationConsumer.class)
class NotificationConsumerTest {
    private static final String NOTIFICATION_CLIENT_URL = "https://util.devi.tools/api/v1/notify";

    @Autowired
    NotificationConsumer notificationConsumer;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldSendNotification() throws JsonProcessingException {
        var expectedNotification = getExpectedNotification();
        var expectedNotificationJson = objectMapper.writeValueAsString(expectedNotification);
        server.expect(requestTo(NOTIFICATION_CLIENT_URL))
                .andRespond(withSuccess(expectedNotificationJson, MediaType.APPLICATION_JSON));

        notificationConsumer.receiveNotifiction(new TransactionDTO(1L, 2L, TEN));

        server.verify();
        assertNotNull(expectedNotification);
        assertNotNull(expectedNotification.getData());
    }

    @Test
    void shouldNotSendNotification() {
        server.expect(requestTo(NOTIFICATION_CLIENT_URL))
                .andRespond(MockRestResponseCreators.withBadGateway());

        var exception = assertThrowsExactly(NotificationException.class,
                () -> notificationConsumer
                        .receiveNotifiction(new TransactionDTO(1L, 2L, TEN)));

        server.verify();
        assertInstanceOf(NotificationException.class, exception);
        assertEquals("Erro ao enviar notificação", exception.getMessage());
    }

    private static Notification getExpectedNotification() {
        var notificationData = new NotificationData();
        notificationData.setMessage("Mensagem teste sucesso");

        var notification = new Notification();
        notification.setStatus("success");
        notification.setData(notificationData);

        return notification;
    }

}