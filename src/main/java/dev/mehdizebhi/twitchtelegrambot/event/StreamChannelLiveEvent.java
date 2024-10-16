package dev.mehdizebhi.twitchtelegrambot.event;

import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class StreamChannelLiveEvent extends ApplicationEvent {

    private final StreamOnlineEvent data;

    public StreamChannelLiveEvent(Object source, StreamOnlineEvent data) {
        super(source);
        this.data = data;
    }

    public StreamChannelLiveEvent(Object source, Clock clock, StreamOnlineEvent data) {
        super(source, clock);
        this.data = data;
    }

    public StreamOnlineEvent getData() {
        return data;
    }
}
