package dev.mehdizebhi.twitchtelegrambot.config;

import dev.mehdizebhi.twitchtelegrambot.kick.KickClient;
import dev.mehdizebhi.twitchtelegrambot.kick.KickOAuthClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class KickClientConfig {

    @Bean
    public KickClient kickClient() {
        WebClient webClient = WebClient.create("https://api.kick.com/public/v1");

        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(webClient))
                .build();
        return proxyFactory.createClient(KickClient.class);
    }

    @Bean
    public KickOAuthClient kickOAuthClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://id.kick.com")
                .defaultHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(KickOAuthClient.class);
    }
}
