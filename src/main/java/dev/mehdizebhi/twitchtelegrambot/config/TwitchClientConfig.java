package dev.mehdizebhi.twitchtelegrambot.config;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.socket.conduit.TwitchConduitSocketPool;
import com.github.twitch4j.eventsub.socket.conduit.exceptions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitchClientConfig {

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Bean
    public TwitchClient twitchClient() {
        return TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableEventSocket(true)
                .withEnableHelix(true)
                .build();
    }

    @Bean
    public IEventSubConduit conduit() throws ShardTimeoutException, ConduitResizeException, CreateConduitException, ConduitNotFoundException, ShardRegistrationException {
        return TwitchConduitSocketPool.create(spec -> {
            spec.clientId(clientId);
            spec.clientSecret(clientSecret);
            spec.poolShards(4);
        });
    }
}
