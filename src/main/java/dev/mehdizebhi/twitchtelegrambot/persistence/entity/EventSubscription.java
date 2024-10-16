package dev.mehdizebhi.twitchtelegrambot.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "event_subscriptions")
public class EventSubscription {

    @Id
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "twitch_id", nullable = false)
    private Stream stream;

    public EventSubscription() {
    }

    public EventSubscription(String id, String name, Stream stream) {
        this.id = id;
        this.name = name;
        this.stream = stream;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
