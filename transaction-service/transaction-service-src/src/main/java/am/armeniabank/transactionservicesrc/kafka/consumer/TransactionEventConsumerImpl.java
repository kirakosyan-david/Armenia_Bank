package am.armeniabank.transactionservicesrc.kafka.consumer;

import am.armeniabank.armeniabankcommon.kafka.consumer.EventConsumer;
import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import am.armeniabank.transactionservicesrc.kafka.model.FreezeEvent;
import am.armeniabank.transactionservicesrc.kafka.model.TransactionEvent;
import am.armeniabank.transactionservicesrc.repository.FreezeRepository;
import am.armeniabank.transactionservicesrc.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumerImpl implements EventConsumer<TransactionEvent> {

    private final TransactionRepository transactionRepository;
    private final FreezeRepository freezeRepository;
    private final KafkaTemplate<String, FreezeEvent> kafkaTemplate;

    @Override
    @KafkaListener(
            topics = "transaction-events",
            groupId = "transaction-service-group",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void handle(TransactionEvent transactionEvent) {
        log.info("Received TransactionEvent: {}", transactionEvent);

        transactionRepository.findById(transactionEvent.getTransactionId()).ifPresent(transaction -> {
            Optional<Freeze> activeFreeze = freezeRepository.findByTransactionIdAndFreezeStatus(
                    transaction.getId(), FreezeStatus.ACTIVE);

            switch (transactionEvent.getStatus()) {
                case CREATED -> {
                    if (activeFreeze.isEmpty()) {
                        Freeze freeze = Freeze.builder()
                                .walletId(transactionEvent.getFromWalletId())
                                .amount(transactionEvent.getAmount())
                                .reason(transactionEvent.getReason() != null ? transactionEvent.getReason() : "Transaction freeze")
                                .freezeStatus(FreezeStatus.ACTIVE)
                                .createdAt(transactionEvent.getTimestamp() != null ? transactionEvent.getTimestamp() : LocalDateTime.now())
                                .transaction(transaction)
                                .build();
                        freezeRepository.save(freeze);
                        log.info("Freeze created for transaction {}: {}", transaction.getId(), freeze.getId());

                        FreezeEvent freezeEvent = FreezeEvent.builder()
                                .freezeId(freeze.getId())
                                .transactionId(transaction.getId())
                                .walletId(freeze.getWalletId())
                                .amount(freeze.getAmount())
                                .freezeStatus(FreezeStatus.ACTIVE)
                                .createdAt(freeze.getCreatedAt())
                                .build();
                        kafkaTemplate.send("freeze-events", freezeEvent);
                        log.info("FreezeEvent sent to topic freeze-events: {}", freezeEvent);
                    } else {
                        log.info("Active freeze already exists for transaction {}", transaction.getId());
                    }
                }
                case COMPLETED, FAILED, ROLLED_BACK -> {
                    activeFreeze.ifPresent(freeze -> {
                        freeze.setFreezeStatus(FreezeStatus.RELEASED);
                        freeze.setReleasedAt(LocalDateTime.now());
                        freezeRepository.save(freeze);
                        log.info("Freeze released for transaction {}: {}", transaction.getId(), freeze.getId());

                        FreezeEvent freezeEvent = FreezeEvent.builder()
                                .freezeId(freeze.getId())
                                .transactionId(transaction.getId())
                                .walletId(freeze.getWalletId())
                                .amount(freeze.getAmount())
                                .freezeStatus(FreezeStatus.RELEASED)
                                .createdAt(freeze.getCreatedAt())
                                .build();
                        kafkaTemplate.send("freeze-events", freezeEvent);
                        log.info("FreezeEvent sent to topic freeze-events: {}", freezeEvent);
                    });
                    if (activeFreeze.isEmpty()) {
                        log.info("No active freeze to release for transaction {}", transaction.getId());
                    }
                }
                default -> log.info("Transaction status {} not handled for transaction {}", transactionEvent.getStatus(), transaction.getId());
            }
        });
    }
}
