package dev.mehdizebhi.twitchtelegrambot.kick;

public enum KickSubscriptionEvent {
    LIVESTREAM_STATUS_UPDATED("livestream.status.updated");

    public String name;

    KickSubscriptionEvent(String name) {
        this.name = name;
    }
}
