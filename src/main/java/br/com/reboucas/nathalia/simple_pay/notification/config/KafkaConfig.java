package br.com.reboucas.nathalia.simple_pay.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    private static final String TRANSFER_NOTIFICATION_TOPIC = "transfer-notification";
    private static final Integer PARTITION_COUNT = 1;
    private static final Integer REPLICA_COUNT = 1;

    @Bean
    public NewTopic transferNotificationTopic() {
        return TopicBuilder
                .name(TRANSFER_NOTIFICATION_TOPIC)
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .build();
    }
}
