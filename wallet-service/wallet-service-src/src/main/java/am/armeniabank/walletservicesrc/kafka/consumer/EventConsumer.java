package am.armeniabank.walletservicesrc.kafka.consumer;

public interface EventConsumer <E>{

    void handle(E e);
}