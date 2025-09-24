package am.armeniabank.transactionservicesrc.kafka.consumer.impl;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import am.armeniabank.transactionservicesrc.kafka.consumer.EventConsumer;
import am.armeniabank.transactionservicesrc.kafka.model.FreezeEvent;
import am.armeniabank.transactionservicesrc.repository.FreezeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FreezeEventConsumerImpl implements EventConsumer<FreezeEvent> {

    private final FreezeRepository freezeRepository;

    @Override
    @KafkaListener(topics = "freeze-events", groupId = "transaction-service-group")
    public void handle(FreezeEvent freezeEvent) {
        log.info("Received FreezeEvent: {}", freezeEvent);

        freezeRepository.findById(freezeEvent.getFreezeId()).ifPresent(freeze -> {
            freeze.setFreezeStatus(freezeEvent.getFreezeStatus());
            if (freezeEvent.getFreezeStatus() == FreezeStatus.RELEASED) {
                freeze.setReleasedAt(freezeEvent.getCreatedAt());
            }
            freezeRepository.save(freeze);
            log.info("Freeze updated: {} -> {}", freeze.getId(), freeze.getFreezeStatus());
        });
    }
}
