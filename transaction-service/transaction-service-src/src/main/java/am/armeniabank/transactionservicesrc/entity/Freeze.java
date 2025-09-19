package am.armeniabank.transactionservicesrc.entity;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "freezes")
public class Freeze {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FreezeStatus freezeStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime releasedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;
}
