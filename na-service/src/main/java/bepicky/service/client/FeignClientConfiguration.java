package bepicky.service.client;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfiguration {

    private static final int TIMEOUT = 30000;

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(TIMEOUT, TimeUnit.MILLISECONDS, TIMEOUT, TimeUnit.MILLISECONDS, true);
    }
}
