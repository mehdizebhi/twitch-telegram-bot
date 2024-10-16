package dev.mehdizebhi.twitchtelegrambot.internal;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.eventsub.socket.IEventSubSocket;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class EventSubWebsocket {

    private final IEventSubSocket eventSocket;

    public EventSubWebsocket(ITwitchClient twitchClient) {
        this.eventSocket = twitchClient.getEventSocket();
    }

    public void registerStreamOnline(String broadcasterUserId) {
        eventSocket.register(
                SubscriptionTypes.STREAM_ONLINE.prepareSubscription(
                        b -> b.broadcasterUserId(broadcasterUserId).build(),
                        null
                )
        );
    }

    public <E> void registerEventHandler(Class<E> event, Consumer<E> consumer) {
        eventSocket.getEventManager().onEvent(event, consumer);
    }
}
