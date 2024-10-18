package dev.mehdizebhi.twitchtelegrambot.bot;

import com.github.twitch4j.TwitchClient;
import dev.mehdizebhi.twitchtelegrambot.constant.BotMessage;
import dev.mehdizebhi.twitchtelegrambot.constant.MessageTemplate;
import dev.mehdizebhi.twitchtelegrambot.internal.StreamService;
import dev.mehdizebhi.twitchtelegrambot.util.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

@Component
public class TwinifyBot extends AbilityBot {

    private final TwitchClient twitchClient;
    private final StreamService streamService;

    public TwinifyBot(
            TwitchClient twitchClient,
            StreamService streamService,
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.bot-username}") String botUsername) {
        super(new OkHttpTelegramClient(botToken), botUsername);
        this.twitchClient = twitchClient;
        this.streamService = streamService;
    }

    public Ability add() {
        return Ability.builder()
                .name("add")
                .info("add stream channel to telegram group")
                .locality(Locality.GROUP)
                .privacy(Privacy.GROUP_ADMIN)
                .action(ctx -> {
                    try {
                        var twitchUsername = ctx.firstArg();
                        var userList = twitchClient.getHelix()
                                .getUsers(null, null, List.of(twitchUsername))
                                .execute()
                                .getUsers();
                        if (!userList.isEmpty()) {
                            var twitchId = userList.getFirst().getId();
                            streamService.addGroup(twitchId, ctx.chatId());
                            silent.send(BotMessage.TWITCH_NOTIFICATION_ADDED.formatted(userList.getFirst().getDisplayName()), ctx.chatId());
                        } else {
                            silent.send(BotMessage.TWITCH_USERNAME_NOT_FOUND.formatted(twitchUsername), ctx.chatId());
                        }
                    } catch (IllegalStateException exception) {
                        silent.send(BotMessage.NO_USERNAME_WAS_FOUND, ctx.chatId());
                    }
                })
                .build();
    }

    public Ability channel() {
        return Ability.builder()
                .name("channel")
                .info("add channel to telegram group")
                .locality(Locality.GROUP)
                .privacy(Privacy.GROUP_ADMIN)
                .action(ctx -> {
                    try {
                        var channelId = ctx.firstArg();
                        streamService.addChannelIdToGroup(ctx.chatId(), channelId);
                        silent.send(BotMessage.TELEGRAM_CHANNEL_ADDED, ctx.chatId());
                    } catch (IllegalStateException exception) {
                        silent.send("Please enter the channel id after the command", ctx.chatId());
                    }
                })
                .build();
    }

    public Ability remove() {
        return Ability.builder()
                .name("remove")
                .info("remove stream channel from telegram group")
                .locality(Locality.GROUP)
                .privacy(Privacy.GROUP_ADMIN)
                .action(ctx -> {
                    try {
                        var twitchUsername = ctx.firstArg();
                        var userList = twitchClient.getHelix()
                                .getUsers(null, null, List.of(twitchUsername))
                                .execute()
                                .getUsers();
                        if (!userList.isEmpty()) {
                            var twitchId = userList.getFirst().getId();
                            streamService.removeGroup(twitchId, ctx.chatId());
                            silent.send(BotMessage.TWITCH_NOTIFICATION_REMOVED, ctx.chatId());
                        } else {
                            silent.send(BotMessage.TWITCH_USERNAME_NOT_FOUND.formatted(twitchUsername), ctx.chatId());
                        }
                    } catch (IllegalStateException exception) {
                        silent.send(BotMessage.NO_USERNAME_WAS_FOUND, ctx.chatId());
                    }
                })
                .build();
    }

    public Ability status() {
        return Ability.builder()
                .name("status")
                .info("check status of specific twitch channel")
                .locality(Locality.GROUP)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    try {
                        var twitchUsername = ctx.firstArg();
                        var userList = twitchClient.getHelix()
                                .getUsers(null, null, List.of(twitchUsername))
                                .execute()
                                .getUsers();
                        if (!userList.isEmpty()) {
                            var twitchId = userList.getFirst().getId();
                            var displayName = userList.getFirst().getDisplayName();
                            var streamList = twitchClient.getHelix()
                                    .getStreams(null, null, null, 1, null, null, List.of(twitchId), null)
                                    .execute()
                                    .getStreams();
                            if (!streamList.isEmpty()) {
                                var stream = streamList.getFirst();
                                var caption = MessageTemplate.STREAM_LIVE_STATUS.formatted(
                                        stream.getUserName(),
                                        stream.getTitle(),
                                        stream.getGameName(),
                                        stream.getViewerCount(),
                                        TimeUtil.formatUptime(stream.getUptime()),
                                        stream.getUserLogin());

                                telegramClient.executeAsync(
                                        SendPhoto.builder()
                                                .chatId(ctx.chatId())
                                                .photo(new InputFile(stream.getThumbnailUrl(1920, 1080)))
                                                .caption(caption)
                                                .build()
                                );
                            } else {
                                silent.send(BotMessage.STREAM_NOT_LIVE.formatted(displayName), ctx.chatId());
                            }
                        } else {
                            silent.send(BotMessage.TWITCH_USERNAME_NOT_FOUND.formatted(twitchUsername), ctx.chatId());
                        }
                    } catch (IllegalStateException exception) {
                        silent.send(BotMessage.NO_USERNAME_WAS_FOUND, ctx.chatId());
                    }
                })
                .build();
    }

    @Override
    public long creatorId() {
        return 7545312668L;
    }
}
