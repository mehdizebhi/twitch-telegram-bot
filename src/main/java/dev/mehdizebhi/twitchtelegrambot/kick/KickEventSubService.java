package dev.mehdizebhi.twitchtelegrambot.kick;

import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import dev.mehdizebhi.twitchtelegrambot.persistence.repository.EventSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KickEventSubService {

    private final KickClient kickClient;
    private final KickTokenManager tokenManager;
    private EventSubscriptionRepository  eventSubscriptionRepository;

    public KickEventSubService(KickClient kickClient, KickTokenManager tokenManager,  EventSubscriptionRepository eventSubscriptionRepository) {
        this.kickClient = kickClient;
        this.tokenManager = tokenManager;
        this.eventSubscriptionRepository = eventSubscriptionRepository;
    }

    public Optional<String> registerStreamOnline(Long broadcasterUserId) {
        try {
            var subscription = kickClient.subscribe(
                    new KickDTO.KickSubscribeRequest(
                            broadcasterUserId,
                            List.of(new KickDTO.KickEventInfo(KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name, 1)),
                            "webhook"),
                    tokenManager.getAccessToken()
            );
            return Optional.of(subscription.data().getFirst().subscriptionId());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean removeSubscription(String subscriptionId) {
        try {
            kickClient.unsubscribe(List.of(subscriptionId), tokenManager.getAccessToken());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeAllSubscriptions() {
        try {
            eventSubscriptionRepository
                    .findByName(KickSubscriptionEvent.LIVESTREAM_STATUS_UPDATED.name)
                    .stream().map(EventSubscription::getId)
                    .forEach(this::removeSubscription);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
