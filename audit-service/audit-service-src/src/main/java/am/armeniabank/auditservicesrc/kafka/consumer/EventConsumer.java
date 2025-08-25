package am.armeniabank.auditservicesrc.kafka.consumer;


public interface EventConsumer<E>{
    void handle(E e);
}
