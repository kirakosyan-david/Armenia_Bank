package am.armeniabank.walletservicesrc.kafka.producer.impl;

import am.armeniabank.armeniabankcommon.kafka.producer.EventProducer;
import am.armeniabank.walletservicesrc.kafka.model.WalletEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletProducer implements EventProducer<WalletEvent> {

    private final KafkaTemplate<String, WalletEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.wallet-events}")
    private String topic;

    @Override
    public void handle(WalletEvent event) {
        kafkaTemplate.send(topic, event.getWalletId().toString(), event);
    }
}
