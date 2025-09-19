package am.armeniabank.transactionservicesrc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableFeignClients(basePackages = "am.armeniabank.transactionserviceapi.contract")
public class TransactionServiceSrcApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceSrcApplication.class, args);
    }

}
