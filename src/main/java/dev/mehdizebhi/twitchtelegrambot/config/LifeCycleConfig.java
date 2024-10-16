package dev.mehdizebhi.twitchtelegrambot.config;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import dev.mehdizebhi.twitchtelegrambot.event.StreamChannelLiveEvent;
import dev.mehdizebhi.twitchtelegrambot.internal.AuthenticationService;
import dev.mehdizebhi.twitchtelegrambot.internal.EventSubConduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@Configuration
public class LifeCycleConfig {

    @Autowired private ITwitchClient twitchClient;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private EventSubConduit eventSubConduit;
    @Autowired private ApplicationEventPublisher eventPublisher;

    @Value("${twitch.username}")
    private String twitchUsername;

    @EventListener(ContextRefreshedEvent.class)
    public void applicationReady() {
        subscribeStreamOnlineEvent();
    }

    private void subscribeStreamOnlineEvent() {
        authenticationService.accessToken().ifPresent(accessToken -> {
            var list = twitchClient.getHelix().getUsers(accessToken, null, List.of(twitchUsername)).execute();
            if (!list.getUsers().isEmpty()) {
                eventSubConduit.registerStreamOnline(list.getUsers().getFirst().getId());
                eventSubConduit.registerEventHandler(StreamOnlineEvent.class, e -> eventPublisher.publishEvent(new StreamChannelLiveEvent(this, e)));
            }
        });
    }
}
