package dev.mehdizebhi.twitchtelegrambot.service;

import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import dev.mehdizebhi.twitchtelegrambot.kick.KickEventSubService;
import dev.mehdizebhi.twitchtelegrambot.kick.KickSubscriptionEvent;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.Stream;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.StreamType;
import dev.mehdizebhi.twitchtelegrambot.persistence.entity.TelegramGroup;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.EventSubscriptionRepository;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.StreamRepository;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.TelegramGroupRepository;
import dev.mehdizebhi.twitchtelegrambot.twitch.EventSubConduit;
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
    private final KickEventSubService kickEventSubService;

    public StreamService(
            StreamRepository streamRepository,
            TelegramGroupRepository telegramGroupRepository,
            EventSubConduit eventSubConduit,
            EventSubscriptionRepository eventSubscriptionRepository,
            KickEventSubService kickEventSubService) {
        this.streamRepository = streamRepository;
        this.telegramGroupRepository = telegramGroupRepository;
        this.eventSubConduit = eventSubConduit;
        this.eventSubscriptionRepository = eventSubscriptionRepository;
        this.kickEventSubService = kickEventSubService;
    }

    @Transactional
    public void addGroup(String streamId, Long chatId, StreamType type) {
        var groupOpt = telegramGroupRepository.findByChatId(chatId);
        TelegramGroup group;
        if (groupOpt.isEmpty()) {
            group = new TelegramGroup(chatId);
            group = telegramGroupRepository.save(group);
        } else {
            group = groupOpt.get();
        }
        final TelegramGroup finalGroup = group;
        streamRepository.findByStreamId(streamId).ifPresentOrElse(
                stream -> {
                    if (!stream.groupExists(finalGroup.getChatId())) {
                        stream.addGroup(finalGroup);
                        switch (type) {
                            case TWITCH: {
                                if (!stream.eventSubscriptionExists(SubscriptionTypes.STREAM_ONLINE.getName())) {
                                    eventSubConduit.registerStreamOnline(streamId).ifPresent(subscriptionId -> {
                                        var subscription = eventSubscriptionRepository
                                                .save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), stream));
                                        stream.addSubscription(subscription);
                                    });
                                }
                                streamRepository.save(stream);
                            }
                            case KICK: {
                                if (!stream.eventSubscriptionExists(KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name)) {
                                    kickEventSubService.registerStreamOnline(Long.valueOf(streamId)).ifPresent(subscriptionId -> {
                                        var subscription = eventSubscriptionRepository
                                                .save(new EventSubscription(subscriptionId, KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name, stream));
                                        stream.addSubscription(subscription);
                                    });
                                }
                                streamRepository.save(stream);
                            }
                        }
                    }
                },
                () -> {
                    var stream = new Stream();
                    stream.setStreamId(streamId);
                    stream.setType(type);
                    stream.addGroup(finalGroup);
                    stream = streamRepository.save(stream);
                    final Stream finalStream = stream;
                    switch (type) {
                        case TWITCH: {
                            eventSubConduit.registerStreamOnline(streamId).ifPresent(subscriptionId -> {
                                var subscription = eventSubscriptionRepository
                                        .save(new EventSubscription(subscriptionId, SubscriptionTypes.STREAM_ONLINE.getName(), finalStream));
                                finalStream.addSubscription(subscription);
                            });
                            streamRepository.save(finalStream);
                        }
                        case KICK: {
                            kickEventSubService.registerStreamOnline(Long.valueOf(streamId)).ifPresent(subscriptionId -> {
                                var subscription = eventSubscriptionRepository
                                        .save(new EventSubscription(subscriptionId, KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name, finalStream));
                                finalStream.addSubscription(subscription);
                            });
                            streamRepository.save(finalStream);
                        }
                    }
                });
    }

    @Transactional
    public void removeGroup(String streamId, Long chatId) {
        streamRepository.findByStreamId(streamId).ifPresent(stream -> {
            var groupOpt = telegramGroupRepository.findByChatId(chatId);
            if (groupOpt.isPresent()) {
                stream.removeGroup(groupOpt.get());
                groupOpt.get().removeStream(stream);
                var savedStream = streamRepository.save(stream);
                // TODO: Remove all eventsub subscription if there is no group for this twitch stream (need to test)
                if (savedStream.getGroups().isEmpty()) {
                    savedStream.getSubscriptions().forEach(eventSubscription -> {
                        switch (savedStream.getType()) {
                            case TWITCH -> eventSubConduit.removeSubscription(eventSubscription.getId());
                            case KICK -> kickEventSubService.removeSubscription(eventSubscription.getId());
                        }
                    });
                    eventSubscriptionRepository.deleteAllByStream_StreamId(streamId);
                }
            }
        });
    }

    public void removeAllEventSubscription() {
        eventSubConduit.removeAllSubscriptions();
        kickEventSubService.removeAllSubscriptions();
        eventSubscriptionRepository.deleteAll();
    }

    public Set<TelegramGroup> getGroupsByStreamId(String streamId) {
        return streamRepository.findByStreamId(streamId)
                .map(Stream::getGroups)
                .orElseGet(Set::of);
    }

    public Set<String> getAllStreamIds(StreamType type) {
        return streamRepository.findAll()
                .stream()
                .filter(stream -> stream.getType() == type)
                .map(Stream::getStreamId)
                .collect(Collectors.toSet());
    }

    public Optional<Stream> getById(String streamId) {
        return streamRepository.findByStreamId(streamId);
    }

    public void addChannelIdToGroup(Long chatId, String channelId) {
        telegramGroupRepository.findByChatId(chatId)
                .ifPresent(telegramGroup -> {
                    telegramGroup.setChannelId(channelId);
                    telegramGroupRepository.save(telegramGroup);
                });
    }
}
