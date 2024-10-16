package dev.mehdizebhi.twitchtelegrambot.persistence.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "streams")
public class Stream {

    @Id
    private String twitchId;

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

    public Stream(String twitchId, Set<TelegramGroup> groups) {
        this.twitchId = twitchId;
        this.groups = groups;
    }

    public Stream(String twitchId, Set<TelegramGroup> groups, Set<EventSubscription> subscriptions) {
        this.twitchId = twitchId;
        this.groups = groups;
        this.subscriptions = subscriptions;
    }

    public String getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(String twitchId) {
        this.twitchId = twitchId;
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
