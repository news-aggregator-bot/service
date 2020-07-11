package vlad110kg.news.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients(basePackages = {"vlad110kg.news.aggregator.client"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
@EnableDiscoveryClient
@EnableJpaRepositories
public class NAService {

    public static void main(String[] args) {
        SpringApplication.run(NAService.class, args);
    }
}
