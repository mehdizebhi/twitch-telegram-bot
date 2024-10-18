package dev.mehdizebhi.twitchtelegrambot.constant;

public class MessageTemplate {

    public static final String STREAM_LIVE = """
            %s is now live on Twitch!
            %s
            Game: %s
            Viewers: %s
            Link: https://twitch.tv/%s
            """;

    public static final String STREAM_LIVE_STATUS = """
            %s is now live on Twitch!
            %s
            Game: %s
            Viewers: %s
            Uptime: %s
            Link: https://twitch.tv/%s
            """;
}
