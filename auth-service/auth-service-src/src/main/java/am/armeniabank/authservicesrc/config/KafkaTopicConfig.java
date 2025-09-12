package am.armeniabank.authservicesrc.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configs = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic authEventsTopic(@Value("${spring.kafka.topic.auth-events}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auditEventsTopic(@Value("${spring.kafka.topic.audit-events}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public boolean checkTopicsExist(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                                    @Value("${spring.kafka.topic.auth-events}") String authTopic,
                                    @Value("${spring.kafka.topic.audit-events}") String auditTopic) throws ExecutionException, InterruptedException {
        try (AdminClient client = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            var existingTopics = client.listTopics().names().get();
            System.out.println("Existing topics: " + existingTopics);
            System.out.println(authTopic + " exists? " + existingTopics.contains(authTopic));
            System.out.println(auditTopic + " exists? " + existingTopics.contains(auditTopic));
        }
        return true;
    }

}
