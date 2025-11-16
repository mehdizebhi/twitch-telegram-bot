package dev.mehdizebhi.twitchtelegrambot.persistence.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "streams")
public class Stream {

    @Id
    private String streamId;

    @Enumerated(EnumType.STRING)
    private StreamType type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "group_streams",
            joinColumns = {@JoinColumn(name = "stream_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    private Set<TelegramGroup> groups;

    @OneToMany(mappedBy = "stream", fetch = FetchType.EAGER)
    private Set<EventSubscription> subscriptions;

    public Stream() {}

    public Stream(String streamId, Set<TelegramGroup> groups, StreamType type) {
        this.streamId = streamId;
        this.groups = groups;
        this.type = type;
    }

    public Stream(String streamId, Set<TelegramGroup> groups, Set<EventSubscription> subscriptions, StreamType type) {
        this.streamId = streamId;
        this.groups = groups;
        this.subscriptions = subscriptions;
        this.type = type;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String twitchId) {
        this.streamId = twitchId;
    }

    public Set<TelegramGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<TelegramGroup> groups) {
        this.groups = groups;
    }

    public Set<EventSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<EventSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public StreamType getType() {
        return type;
    }

    public void setType(StreamType type) {
        this.type = type;
    }

    public void addGroup(TelegramGroup group) {
        if (groups == null) {
            groups = new HashSet<>();
        }
        groups.add(group);
    }

    public void removeGroup(TelegramGroup group) {
        if (groups != null) {
            groups.remove(group);
        }
    }

    public boolean groupExists(Long chatId) {
        return groups != null && groups.stream().anyMatch(group -> group.getChatId().equals(chatId));
    }

    public boolean eventSubscriptionExists(String name) {
        return subscriptions != null && subscriptions.stream().anyMatch(subscription -> subscription.getName().equals(name));
    }

    public void addSubscription(EventSubscription subscription) {
        if (subscriptions == null) {
            subscriptions = new HashSet<>();
        }
        subscriptions.add(subscription);
    }

    public void removeSubscription(EventSubscription subscription) {
        if (subscriptions != null) {
            subscriptions.remove(subscription);
        }
    }
}
