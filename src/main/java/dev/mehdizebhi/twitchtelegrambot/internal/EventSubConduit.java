package dev.mehdizebhi.twitchtelegrambot.internal;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class EventSubConduit {

    @Autowired private IEventSubConduit conduit;
    @Autowired private TwitchClient twitchClient;

    public Optional<String> registerStreamOnline(String broadcasterUserId) {
        try {
            var subscription = conduit.register(
                    SubscriptionTypes.STREAM_ONLINE.prepareSubscription(
                            b -> b.broadcasterUserId(broadcasterUserId).build(),
                            null
                    )
            );
            return Optional.of(subscription.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean removeSubscription(String subscriptionId) {
        try {
            twitchClient.getHelix().deleteEventSubSubscription(null, subscriptionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeAllSubscriptions() {
        try {
            twitchClient.getEventManager().getActiveSubscriptions().forEach(subscription -> removeSubscription(subscription.getId()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <E> void registerEventHandler(Class<E> event, Consumer<E> consumer) {
        conduit.getEventManager().onEvent(event, consumer);
    }
}
