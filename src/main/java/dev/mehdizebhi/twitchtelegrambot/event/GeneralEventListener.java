package dev.mehdizebhi.twitchtelegrambot.event;

import com.github.twitch4j.TwitchClient;
import dev.mehdizebhi.twitchtelegrambot.bot.TwinifyBot;
import dev.mehdizebhi.twitchtelegrambot.constant.MessageTemplate;
import dev.mehdizebhi.twitchtelegrambot.kick.KickClient;
import dev.mehdizebhi.twitchtelegrambot.kick.KickTokenManager;
import dev.mehdizebhi.twitchtelegrambot.service.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

@Component
public class GeneralEventListener {

    @Autowired private TwitchClient twitchClient;
    @Autowired private KickClient kickClient;
    @Autowired private KickTokenManager kickTokenManager;
    @Autowired private TwinifyBot twinifyBot;
    @Autowired private StreamService streamService;

    private Logger logger = LoggerFactory.getLogger(GeneralEventListener.class);

    @Async
    @EventListener(StreamChannelLiveEvent.class)
    public void streamChannelLiveEventListener(final StreamChannelLiveEvent event) {
        switch (event) {
            case TwitchStreamChannelLiveEvent e -> {
                var streamList = twitchClient.getHelix()
                        .getStreams(null, null, null, 1, null, null, List.of(e.data().getBroadcasterUserId()), null)
                        .execute();
                if (!streamList.getStreams().isEmpty()) {
                    var stream = streamList.getStreams().getFirst();
                    var caption = MessageTemplate.STREAM_LIVE.formatted(
                            stream.getUserName(),
                            stream.getTitle(),
                            stream.getGameName(),
                            stream.getViewerCount(),
                            stream.getUserLogin());
                    streamService.getGroupsByStreamId(stream.getUserId()).forEach(group -> {
                        twinifyBot.getTelegramClient()
                                .executeAsync(
                                        SendPhoto.builder()
                                                .chatId(group.getChatId())
                                                .photo(new InputFile(stream.getThumbnailUrl(1920, 1080)))
                                                .caption(caption)
                                                .build()
                                );
                        if (group.getChannelId() != null) {
                            twinifyBot.getTelegramClient()
                                    .executeAsync(
                                            SendPhoto.builder()
                                                    .chatId(group.getChannelId())
                                                    .photo(new InputFile(stream.getThumbnailUrl(1920, 1080)))
                                                    .caption(caption)
                                                    .build()
                                    );
                        }
                    });
                }
            }
            case KickStreamChannelLiveEvent e -> {
                var channelList = kickClient.getChannelByUserId(e.data().broadcaster().userId(), kickTokenManager.getAccessToken()).data();
                if (!channelList.isEmpty()) {
                    var channel = channelList.getFirst();
                    var caption = MessageTemplate.KICK_STREAM_LIVE.formatted(
                            channel.slug(),
                            channel.streamTitle(),
                            channel.category().name(),
                            channel.stream().viewerCount(),
                            channel.slug());
                    streamService.getGroupsByStreamId(String.valueOf(channel.broadcasterUserId())).forEach(group -> {
                        twinifyBot.getTelegramClient()
                                .executeAsync(
                                        SendPhoto.builder()
                                                .chatId(group.getChatId())
                                                .photo(new InputFile(channel.stream().thumbnail()))
                                                .caption(caption)
                                                .build()
                                );
                        if (group.getChannelId() != null) {
                            twinifyBot.getTelegramClient()
                                    .executeAsync(
                                            SendPhoto.builder()
                                                    .chatId(group.getChannelId())
                                                    .photo(new InputFile(channel.stream().thumbnail()))
                                                    .caption(caption)
                                                    .build()
                                    );
                        }
                    });
                }
            }
            case null, default -> logger.warn("Unknown event " + event);
        }
    }
}
