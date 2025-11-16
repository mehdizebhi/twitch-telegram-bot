package dev.mehdizebhi.twitchtelegrambot.event;

import dev.mehdizebhi.twitchtelegrambot.kick.KickDTO;

public record KickStreamChannelLiveEvent(KickDTO.LivestreamStatusUpdatedEvent data) implements StreamChannelLiveEvent {
}
