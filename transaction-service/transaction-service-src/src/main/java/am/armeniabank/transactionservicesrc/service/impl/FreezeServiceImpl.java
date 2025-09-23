package am.armeniabank.transactionservicesrc.service.impl;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import am.armeniabank.transactionserviceapi.response.FreezeResponse;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import am.armeniabank.transactionservicesrc.exception.custam.FreezeNotFoundException;
import am.armeniabank.transactionservicesrc.exception.custam.FreezeOperationException;
import am.armeniabank.transactionservicesrc.exception.custam.InvalidFreezeStateException;
import am.armeniabank.transactionservicesrc.exception.custam.UserNotFoundException;
import am.armeniabank.transactionservicesrc.kafka.model.FreezeEvent;
import am.armeniabank.transactionservicesrc.mapper.FreezeMapper;
import am.armeniabank.transactionservicesrc.repository.FreezeRepository;
import am.armeniabank.transactionservicesrc.service.FreezeService;
import am.armeniabank.transactionservicesrc.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreezeServiceImpl implements FreezeService {

    private final FreezeRepository freezeRepository;
    private final FreezeMapper freezeMapper;
    private final KafkaTemplate<String, FreezeEvent> kafkaTemplate;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "activeFreezes", key = "#transaction.userId")
    public FreezeResponse createFreeze(Transaction transaction, BigDecimal amount, String reason, String token) {
        if (transaction == null) {
            throw new UserNotFoundException("Transaction cannot be null");
        }
        if (transaction.getFromWalletId() == null) {
            throw new UserNotFoundException("WalletId cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserNotFoundException("Amount must be positive");
        }

        Freeze freeze = Freeze.builder()
                .walletId(transaction.getFromWalletId())
                .transaction(transaction)
                .amount(amount)
                .reason(reason)
                .freezeStatus(FreezeStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        Freeze savedFreeze = freezeRepository.save(freeze);

        log.info("Created freeze {} for wallet {} amount {}", savedFreeze.getId(), transaction.getFromWalletId(), amount);

        sendFreezeEvent(savedFreeze);

        return freezeMapper.mapToFreezeResponse(savedFreeze);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"activeFreezes", "freezeByTransaction"},
            key = "#freeze.transaction.userId",
            allEntries = true)
    public FreezeResponse releaseFreeze(Freeze freeze, String token) {
        if (freeze == null) {
            throw new FreezeNotFoundException("Freeze not found or null");
        }

        if (freeze.getFreezeStatus() != FreezeStatus.ACTIVE &&
                freeze.getFreezeStatus() != FreezeStatus.CONSUMED) {
            throw new InvalidFreezeStateException(
                    "Freeze must be ACTIVE or CONSUMED to be released. Current: " + freeze.getFreezeStatus());
        }

        freeze.setFreezeStatus(FreezeStatus.RELEASED);
        freeze.setReleasedAt(LocalDateTime.now());
        Freeze saved = freezeRepository.save(freeze);

        log.info("Released freeze {} for wallet {}", saved.getId(), saved.getWalletId());

        sendFreezeEvent(saved);

        return freezeMapper.mapToFreezeResponse(saved);
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"activeFreezes", "freezeByTransaction"}, allEntries = true)
    public void completeFreeze(Freeze freeze) {
        if (freeze == null) {
            throw new FreezeNotFoundException("Freeze not found or null");
        }

        if (freeze.getFreezeStatus() != FreezeStatus.ACTIVE &&
                freeze.getFreezeStatus() != FreezeStatus.RELEASED) {
            throw new InvalidFreezeStateException(
                    "Freeze must be ACTIVE or RELEASED to be completed. Current: " + freeze.getFreezeStatus()
            );
        }

        try {
            freeze.setFreezeStatus(FreezeStatus.CONSUMED);
            freeze.setReleasedAt(LocalDateTime.now());
            Freeze saved = freezeRepository.save(freeze);
            log.info("Completed freeze {} for wallet {}", saved.getId(), saved.getWalletId());

            sendFreezeEvent(saved);

        } catch (Exception ex) {
            throw new FreezeOperationException("Failed to complete freeze " + freeze.getId(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "freezeByTransaction", key = "#transactionId")
    public Freeze getFreezeByTransaction(UUID transactionId) {
        return freezeRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new FreezeNotFoundException("Freeze not found for transaction " + transactionId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "activeFreezes", key = "T(am.armeniabank.transactionservicesrc.util.SecurityUtils).getCurrentUserId()")
    public List<FreezeResponse> getActiveFreezesForCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();

        List<Freeze> freezes = freezeRepository.findAllByTransactionUserIdAndFreezeStatus(userId, FreezeStatus.ACTIVE);

        if (freezes.isEmpty()) {
            log.info("No active freezes found for user {}", userId);
        }

        return freezes.stream()
                .map(freezeMapper::mapToFreezeResponse)
                .toList();
    }

    private void sendFreezeEvent(Freeze freeze) {
        FreezeEvent event = FreezeEvent.builder()
                .freezeId(freeze.getId())
                .transactionId(freeze.getTransaction().getId())
                .walletId(freeze.getWalletId())
                .freezeStatus(freeze.getFreezeStatus())
                .amount(freeze.getAmount())
                .createdAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send("freeze-events", event);
        log.info("Sent FreezeEvent to Kafka: {}", event);
    }
}