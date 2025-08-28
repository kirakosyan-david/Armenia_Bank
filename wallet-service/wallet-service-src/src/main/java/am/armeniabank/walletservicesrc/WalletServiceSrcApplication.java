package am.armeniabank.walletservicesrc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "am.armeniabank.walletserviceapi.contract")
public class WalletServiceSrcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletServiceSrcApplication.class, args);
    }

}
