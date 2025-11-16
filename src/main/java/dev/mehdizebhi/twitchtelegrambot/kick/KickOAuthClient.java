package dev.mehdizebhi.twitchtelegrambot.kick;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface KickOAuthClient {

    @PostExchange("https://id.kick.com/oauth/token")
    KickDTO.KickAccessTokenResponse requestToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType);
}
