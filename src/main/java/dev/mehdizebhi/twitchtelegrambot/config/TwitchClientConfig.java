package dev.mehdizebhi.twitchtelegrambot.config;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.socket.conduit.TwitchConduitSocketPool;
import com.github.twitch4j.eventsub.socket.conduit.exceptions.*;
import com.github.twitch4j.helix.domain.ConduitList;
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
    public IEventSubConduit conduit(TwitchClient twitchClient) throws ShardTimeoutException, ConduitResizeException, CreateConduitException, ConduitNotFoundException, ShardRegistrationException {
        deleteAllConduits(twitchClient);
        return TwitchConduitSocketPool.create(spec -> {
            spec.clientId(clientId);
            spec.clientSecret(clientSecret);
            spec.poolShards(4);
        });
    }

    private void deleteAllConduits(TwitchClient twitchClient) {
        try {
            ConduitList conduits = twitchClient.getHelix().getConduits(null).execute();

            if (conduits.getConduits().isEmpty()) {
                System.out.println("No conduits to delete");
                return;
            }

            System.out.println("Deleting " + conduits.getConduits().size() + " conduits...");

            for (var conduit : conduits.getConduits()) {
                try {
                    twitchClient.getHelix().deleteConduit(null, conduit.getId()).execute();
                    System.out.println("Deleted conduit: " + conduit.getId());
                } catch (Exception e) {
                    System.err.println("Failed to delete conduit " + conduit.getId() + ": " + e.getMessage());
                }
            }

            System.out.println("Cleanup complete!");
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}
