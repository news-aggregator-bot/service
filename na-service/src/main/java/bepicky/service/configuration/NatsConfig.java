package bepicky.service.configuration;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.JetStreamOptions;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
@Slf4j
public class NatsConfig {

    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options opt = new Options.Builder()
            .server("nats://na-ts:4222")
            .connectionName("na-ts")
            .connectionTimeout(Duration.ofSeconds(10))
            .build();
        return Nats.connect(opt);
    }

//    @Bean
//    public StreamInfo newsNotificationStreamInfo(Connection natsConnection)
//        throws IOException, JetStreamApiException {
//        JetStreamManagement jsm = natsConnection.jetStreamManagement(
//            JetStreamOptions.builder()
//                .requestTimeout(Duration.ofSeconds(10))
//                .build()
//        );
//        StreamConfiguration stream = StreamConfiguration.builder()
//            .name("news.notification")
//            .subjects(newsNotificationTopic)
//            .storageType(StorageType.Memory)
//            .build();
//        return jsm.addStream(stream);
//    }
}
