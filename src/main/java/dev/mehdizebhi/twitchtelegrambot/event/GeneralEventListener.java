package dev.mehdizebhi.twitchtelegrambot.event;

import com.github.twitch4j.TwitchClient;
import dev.mehdizebhi.twitchtelegrambot.bot.TwinifyBot;
import dev.mehdizebhi.twitchtelegrambot.constant.MessageTemplate;
import dev.mehdizebhi.twitchtelegrambot.internal.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

@Component
public class GeneralEventListener {

    @Autowired private TwitchClient twitchClient;
    @Autowired private TwinifyBot twinifyBot;
    @Autowired private StreamService streamService;

    @EventListener(StreamChannelLiveEvent.class)
    public void streamChannelLiveEventListener(final StreamChannelLiveEvent event) {
        var streamList = twitchClient.getHelix()
                .getStreams(null, null, null, 1, null, null, List.of(event.getData().getBroadcasterUserId()), null)
                .execute();
        if (!streamList.getStreams().isEmpty()) {
            var stream = streamList.getStreams().getFirst();
            var caption = MessageTemplate.STREAM_LIVE.formatted(
                    stream.getUserName(),
                    stream.getTitle(),
                    stream.getGameName(),
                    stream.getViewerCount(),
                    stream.getUserLogin());
            streamService.getGroupsByTwitchId(stream.getUserId()).forEach(chatId -> {
                twinifyBot.getTelegramClient()
                        .executeAsync(
                                SendPhoto.builder()
                                        .chatId(chatId)
                                        .photo(new InputFile(stream.getThumbnailUrl(1920, 1080)))
                                        .caption(caption)
                                        .build()
                        );
            });
        }
    }
}
