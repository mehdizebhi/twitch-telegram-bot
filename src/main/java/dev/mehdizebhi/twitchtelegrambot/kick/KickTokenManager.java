package dev.mehdizebhi.twitchtelegrambot.kick;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KickTokenManager {

    private final KickOAuthClient kickOAuthClient;
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private LocalDateTime expiresAt;

    public KickTokenManager(KickOAuthClient kickOAuthClient, @Value("${kick.client-id}") String clientId, @Value("${kick.client-secret}") String clientSecret) {
        this.kickOAuthClient = kickOAuthClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || isExpired()) {
            refreshToken();
        }
        return accessToken;
    }

    public void clear() {
        accessToken = null;
        expiresAt = null;
    }

    private boolean isExpired() {
        return expiresAt == null || LocalDateTime.now().isAfter(expiresAt);
    }

    private void refreshToken() {
        KickDTO.KickAccessTokenResponse response = kickOAuthClient.requestToken(clientId, clientSecret, "client_credentials");
        this.accessToken = response.tokenType() + " " + response.accessToken();
        this.expiresAt = LocalDateTime.now().plusSeconds(response.expiresIn());
    }
}
