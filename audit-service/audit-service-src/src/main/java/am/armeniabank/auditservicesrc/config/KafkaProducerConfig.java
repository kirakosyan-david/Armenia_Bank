package am.armeniabank.auditservicesrc.config;


import am.armeniabank.auditservicesrc.kafka.model.AuditEvent;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducerConfig {


    @Bean
    public ProducerFactory<String, AuditEvent> auditEventProducerFactory(KafkaProperties props) {
        Map<String, Object> config = new HashMap<>(props.buildProducerProperties());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AuditEvent> auditEventKafkaTemplate(
            ProducerFactory<String, AuditEvent> pf) {
        return new KafkaTemplate<>(pf);
    }
}
