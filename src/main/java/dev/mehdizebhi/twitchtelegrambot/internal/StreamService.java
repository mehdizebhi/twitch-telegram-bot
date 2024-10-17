package dev.mehdizebhi.twitchtelegrambot.internal;

import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.Stream;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.TelegramGroup;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.EventSubscriptionRepository;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.StreamRepository;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.TelegramGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreamService {

    private final StreamRepository streamRepository;
    private final TelegramGroupRepository telegramGroupRepository;
    private final EventSubConduit eventSubConduit;
    private final EventSubscriptionRepository eventSubscriptionRepository;

    public StreamService(
            StreamRepository streamRepository,
            TelegramGroupRepository telegramGroupRepository,
            EventSubConduit eventSubConduit,
            EventSubscriptionRepository eventSubscriptionRepository) {
        this.streamRepository = streamRepository;
        this.telegramGroupRepository = telegramGroupRepository;
        this.eventSubConduit = eventSubConduit;
        this.eventSubscriptionRepository = eventSubscriptionRepository;
    }

    @Transactional
    public void addGroup(String twitchId, Long chatId) {
        var groupOpt = telegramGroupRepository.findByChatId(chatId);
        TelegramGroup group;
        if (groupOpt.isEmpty()) {
            group = new TelegramGroup(chatId);
            group = telegramGroupRepository.save(group);
        } else {
            group = groupOpt.get();
        }
        final TelegramGroup finalGroup = group;
        streamRepository.findByTwitchId(twitchId).ifPresentOrElse(
                stream -> {
                    if (!stream.groupExists(finalGroup.getChatId())) {
                        stream.addGroup(finalGroup);
                        if (!stream.eventSubscriptionExists(SubscriptionTypes.STREAM_ONLINE.getName())) {
                            eventSubConduit.registerStreamOnline(twitchId).ifPresent(subscriptionId -> {
                                var subscription = eventSubscriptionRepository
                                        .save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), stream));
                                stream.addSubscription(subscription);
                            });
                        }
                        streamRepository.save(stream);
                    }
                },
                () -> {
                    var stream = new Stream();
                    stream.setTwitchId(twitchId);
                    stream.addGroup(finalGroup);
                    stream = streamRepository.save(stream);
                    final Stream finalStream = stream;
                    eventSubConduit.registerStreamOnline(twitchId).ifPresent(subscriptionId -> {
                        var subscription = eventSubscriptionRepository
                                .save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), finalStream));
                        finalStream.addSubscription(subscription);
                    });
                    streamRepository.save(finalStream);
                });
    }

    @Transactional
    public void removeGroup(String twitchId, Long chatId) {
        streamRepository.findByTwitchId(twitchId).ifPresent(stream -> {
            var groupOpt = telegramGroupRepository.findByChatId(chatId);
            if (groupOpt.isPresent()) {
                stream.removeGroup(groupOpt.get());
                groupOpt.get().removeStream(stream);
                var savedStream = streamRepository.save(stream);
                // TODO: Remove all eventsub subscription if there is no group for this twitch stream
                /*if (savedStream.getGroups().isEmpty()) {
                    savedStream.getSubscriptions().forEach(eventSubscription -> {
                        eventSubConduit.removeSubscription(eventSubscription.getId());
                    });
                    eventSubscriptionRepository.deleteAllByStream_TwitchId(twitchId);
                }*/
            }
        });
    }

    public void removeAllEventSubscription() {
        eventSubConduit.removeAllSubscriptions();
        eventSubscriptionRepository.deleteAll();
    }

    public Set<Long> getGroupIdsByTwitchId(String twitchId) {
        return streamRepository.findByTwitchId(twitchId)
                .map(stream -> stream.getGroups().stream().map(TelegramGroup::getChatId).collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    public Set<TelegramGroup> getGroupsByTwitchId(String twitchId) {
        return streamRepository.findByTwitchId(twitchId)
                .map(Stream::getGroups)
                .orElseGet(Set::of);
    }

    public Set<String> getAllTwitchIds() {
        return streamRepository.findAll().stream().map(Stream::getTwitchId).collect(Collectors.toSet());
    }

    public Optional<Stream> getById(String twitchId) {
        return streamRepository.findByTwitchId(twitchId);
    }

    public void addChannelIdToGroup(Long chatId, String channelId) {
        telegramGroupRepository.findByChatId(chatId)
                .ifPresent(telegramGroup -> {
                    telegramGroup.setChannelId(channelId);
                    telegramGroupRepository.save(telegramGroup);
                });
    }
}
