package dev.mehdizebhi.twitchtelegrambot.persistence.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "telegram_groups")
public class TelegramGroup {

    @Id
    private Long chatId;

    private String channelId;

    @ManyToMany(mappedBy = "groups")
    private Set<Stream> streams;

    public TelegramGroup() {
    }

    public TelegramGroup(Long chatId) {
        this.chatId = chatId;
    }

    public TelegramGroup(Long chatId, Set<Stream> streams) {
        this.chatId = chatId;
        this.streams = streams;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Set<Stream> getStreams() {
        return streams;
    }

    public void setStreams(Set<Stream> streams) {
        this.streams = streams;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void addStream(Stream stream) {
        if (streams == null) {
            streams = new HashSet<>();
        }
        streams.add(stream);
    }

    public void removeStream(Stream stream) {
        if (streams != null) {
            streams.remove(stream);
        }
    }
}
