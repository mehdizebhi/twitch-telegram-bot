package dev.mehdizebhi.twitchtelegrambot.internal;

import com.github.twitch4j.eventsub.socket.IEventSubConduit;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class EventSubConduit {

    @Autowired
    private IEventSubConduit conduit;

    public void registerStreamOnline(String broadcasterUserId) {
        conduit.register(
                SubscriptionTypes.STREAM_ONLINE.prepareSubscription(
                        b -> b.broadcasterUserId(broadcasterUserId).build(),
                        null
                )
        );
    }

    public <E> void registerEventHandler(Class<E> event, Consumer<E> consumer) {
        conduit.getEventManager().onEvent(event, consumer);
    }
}
