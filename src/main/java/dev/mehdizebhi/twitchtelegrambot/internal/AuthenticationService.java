package dev.mehdizebhi.twitchtelegrambot.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final WebClient webClient;
    private AuthToken authToken;
    private Long timeOfCreateToken;

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    public AuthenticationService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://id.twitch.tv")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    public Optional<String> accessToken() {
        if (isAuthTokenValid()) {
            return Optional.of(authToken.accessToken());
        } else {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("client_id", clientId);
            data.add("client_secret", clientSecret);
            data.add("grant_type", "client_credentials");
            try {
                authToken = webClient.post()
                        .uri("/oauth2/token")
                        .bodyValue(data)
                        .retrieve()
                        .bodyToMono(AuthToken.class)
                        .block();

                timeOfCreateToken = System.currentTimeMillis() / 1000L;
                return Optional.of(authToken.accessToken());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    private boolean isAuthTokenValid() {
        Long currentTime = System.currentTimeMillis() / 1000;
        return authToken != null && (currentTime - timeOfCreateToken) <= authToken.expiresIn();
    }
}
