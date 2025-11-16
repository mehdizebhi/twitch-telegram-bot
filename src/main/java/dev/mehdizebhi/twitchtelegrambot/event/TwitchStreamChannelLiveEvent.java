package dev.mehdizebhi.twitchtelegrambot.event;

import com.github.twitch4j.eventsub.events.StreamOnlineEvent;

public record TwitchStreamChannelLiveEvent(StreamOnlineEvent data) implements StreamChannelLiveEvent {}