package dev.mehdizebhi.twitchtelegrambot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.util.HashSet;
import java.util.Set;

@Component
public class TwinifyBot extends AbilityBot {

    private final Set<Long> gruopIds;

    public TwinifyBot(
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.bot-username}") String botUsername) {
        super(new OkHttpTelegramClient(botToken), botUsername);
        this.gruopIds = new HashSet<>();
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info("start bot")
                .locality(Locality.GROUP)
                .privacy(Privacy.GROUP_ADMIN)
                .action(ctx -> {
                    if (gruopIds.contains(ctx.chatId())) {
                        silent.send("This bot is already activated on this group.", ctx.chatId());
                    } else {
                        gruopIds.add(ctx.chatId());
                        silent.send("Twitch live notification has been activated for this group.", ctx.chatId());
                    }
                })
                .build();
    }

    @Override
    public long creatorId() {
        return 7956327124L;
    }

    public Set<Long> getGruopIds() {
        return gruopIds;
    }
}
