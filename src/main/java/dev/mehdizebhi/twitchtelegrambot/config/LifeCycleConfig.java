package dev.mehdizebhi.twitchtelegrambot.config;

import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import dev.mehdizebhi.twitchtelegrambot.event.StreamChannelLiveEvent;
import dev.mehdizebhi.twitchtelegrambot.internal.EventSubConduit;
import dev.mehdizebhi.twitchtelegrambot.internal.StreamService;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.EventSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class LifeCycleConfig {

    @Autowired private EventSubConduit eventSubConduit;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private StreamService streamService;
    @Autowired private EventSubscriptionRepository eventSubscriptionRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void applicationReady() {
        subscribeStreamOnlineEvent();
    }

    private void subscribeStreamOnlineEvent() {
        streamService.removeAllEventSubscription();
        streamService.getAllTwitchIds().forEach(twitchId -> {
            eventSubConduit.registerStreamOnline(twitchId).ifPresent(subscriptionId -> {
                streamService.getById(twitchId).ifPresent(stream -> {
                    eventSubscriptionRepository.save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), stream));
                });
            });
        });
        eventSubConduit.registerEventHandler(StreamOnlineEvent.class, e -> eventPublisher.publishEvent(new StreamChannelLiveEvent(this, e)));
    }
}
