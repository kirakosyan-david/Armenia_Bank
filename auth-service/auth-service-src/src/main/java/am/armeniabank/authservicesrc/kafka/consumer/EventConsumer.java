package am.armeniabank.authservicesrc.kafka.consumer;


public interface EventConsumer <E>{
    void handle(E e);
}
