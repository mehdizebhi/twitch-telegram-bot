package dev.mehdizebhi.twitchtelegrambot.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthToken(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        Long expiresIn,
        @JsonProperty("token_type")
        String tokenType
) {
}
