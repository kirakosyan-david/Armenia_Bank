package am.armeniabank.walletservicesrc.service.impl;

import am.armeniabank.armeniabankcommon.contract.UserApi;
import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.armeniabankcommon.response.UserResponse;
import am.armeniabank.walletserviceapi.enums.WalletStatus;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import am.armeniabank.walletservicesrc.exception.custom.WalletAlreadyExistsException;
import am.armeniabank.walletservicesrc.exception.custom.WalletNotFoundException;
import am.armeniabank.walletservicesrc.integration.AuditServiceClient;
import am.armeniabank.walletservicesrc.mapper.WalletMapper;
import am.armeniabank.walletservicesrc.repository.WalletRepository;
import am.armeniabank.walletservicesrc.service.WalletService;
import am.armeniabank.walletservicesrc.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final UserApi userApi;
    private final AuditServiceClient auditClient;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletResponse createWallet(Currency currency) {
        UUID userId = SecurityUtils.getCurrentUserId();
        log.info("Creating wallet for userId={} with currency={}", userId, currency);

        walletRepository.findByUserIdAndCurrency(userId, currency)
                .ifPresent(w -> {
                    log.warn("Wallet already exists for userId={} with currency={}", userId, currency);
                    throw new WalletAlreadyExistsException(userId, currency.name());
                });

        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Wallet walletSaved = walletRepository.save(wallet);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-CREATED");

        log.info("Wallet created successfully with id={} for userId={}", walletSaved.getId(), userId);
        return walletMapper.toWalletResponse(walletSaved, userById);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "wallets", key = "#walletId")
    public WalletResponse getWalletById(UUID walletId) {
        log.debug("Fetching wallet by id={}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found with id={}", walletId);
                    return new WalletNotFoundException(walletId);
                });

        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        log.info("Fetched wallet with id={} for userId={}", walletId, userId);
        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> getWalletsByUserId(UUID userId) {
        log.debug("Fetching wallets for userId={}", userId);

        List<Wallet> wallets = walletRepository.findAllByUserId(userId);
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        log.info("Fetched {} wallets for userId={}", wallets.size(), userId);
        return wallets.stream()
                .map(wallet -> walletMapper.toWalletResponse(wallet, userById))
                .toList();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "wallets", key = "#walletId")
    public WalletResponse blockWallet(UUID walletId) {
        log.info("Blocking wallet with id={}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found for blocking, id={}", walletId);
                    return new WalletNotFoundException(walletId);
                });
        wallet.setStatus(WalletStatus.BLOCKED);
        walletRepository.save(wallet);
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-BLOCKED");

        log.info("Wallet with id={} has been blocked", walletId);
        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "wallets", key = "#walletId")
    public WalletResponse unblockWallet(UUID walletId) {
        log.info("Unblocking wallet with id={}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found for unblocking, id={}", walletId);
                    return new WalletNotFoundException(walletId);
                });
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-UNBLOCKED");

        log.info("Wallet with id={} has been unblocked", walletId);
        return walletMapper.toWalletResponse(wallet, userById);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "wallets", key = "#walletId")
    public WalletResponse closeWallet(UUID walletId) {
        log.info("Closing wallet with id={}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found for closing, id={}", walletId);
                    return new WalletNotFoundException(walletId);
                });
        wallet.setStatus(WalletStatus.CLOSED);
        walletRepository.save(wallet);
        UUID userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        UserResponse userById = userApi.getUserById(userId, "Bearer " + token);

        auditClient.sendAuditWalletEvent(wallet.getId(), userById, "WALLET-CLOSED");

        log.info("Wallet with id={} has been closed", walletId);
        return walletMapper.toWalletResponse(wallet, userById);
    }

}
