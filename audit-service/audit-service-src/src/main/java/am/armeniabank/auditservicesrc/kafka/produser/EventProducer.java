package am.armeniabank.auditservicesrc.kafka.produser;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface EventProducer<T> {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(T event);
}
