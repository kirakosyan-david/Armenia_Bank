package am.armeniabank.transactionservicesrc.kafka.consumer;

public interface EventConsumer<E>{

    void handle(E e);
}