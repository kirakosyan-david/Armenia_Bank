package am.armeniabank.walletservicesrc.kafka.consumer.impl;

import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import am.armeniabank.walletservicesrc.handler.WalletEventHandler;
import am.armeniabank.walletservicesrc.kafka.model.WalletEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventConsumer {

    private final List<WalletEventHandler> handlers;

    @KafkaListener(topics = "${spring.kafka.topic.wallet-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(WalletEvent event) {
        log.info("Consumed wallet operation event: walletId={}, type={}",
                event.getWalletId(), event.getRequest().getWalletOperationType());

        UUID walletId = event.getWalletId();
        WalletOperationType type = event.getRequest().getWalletOperationType();

        handlers.stream()
                .filter(handler -> handler.isHandle(type))
                .forEach(handler -> handler.handle(walletId, event.getRequest()));
    }
}
