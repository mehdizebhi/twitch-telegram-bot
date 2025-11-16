package dev.mehdizebhi.twitchtelegrambot.config;

import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import dev.mehdizebhi.twitchtelegrambot.event.TwitchStreamChannelLiveEvent;
import dev.mehdizebhi.twitchtelegrambot.kick.KickEventSubService;
import dev.mehdizebhi.twitchtelegrambot.kick.KickSubscriptionEvent;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.StreamType;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.EventSubscriptionRepository;
import dev.mehdizebhi.twitchtelegrambot.service.StreamService;
import dev.mehdizebhi.twitchtelegrambot.twitch.EventSubConduit;
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
    @Autowired private KickEventSubService kickEventSubService;

    @EventListener(ContextRefreshedEvent.class)
    public void applicationReady() {
        streamService.removeAllEventSubscription();
        subscribeTwitchStreamOnlineEvent();
        subscribeKickStreamOnlineEvent();
    }
    private void subscribeTwitchStreamOnlineEvent() {
        streamService.getAllStreamIds(StreamType.TWITCH).forEach(streamId -> {
            eventSubConduit.registerStreamOnline(streamId).ifPresent(subscriptionId -> {
                streamService.getById(streamId).ifPresent(stream -> {
                    eventSubscriptionRepository.save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), stream));
                });
            });
        });
        eventSubConduit.registerEventHandler(StreamOnlineEvent.class, e -> eventPublisher.publishEvent(new TwitchStreamChannelLiveEvent(e)));
    }

    private void subscribeKickStreamOnlineEvent() {
        streamService.getAllStreamIds(StreamType.KICK).forEach(streamId -> {
            kickEventSubService.registerStreamOnline(Long.valueOf(streamId)).ifPresent(subscriptionId -> {
                streamService.getById(streamId).ifPresent(stream -> {
                    eventSubscriptionRepository.save(new EventSubscription(subscriptionId, KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name, stream));
                });
            });
        });
    }
}
