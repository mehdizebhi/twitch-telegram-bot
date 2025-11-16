package dev.mehdizebhi.twitchtelegrambot.persistence.repository;

import dev.mehdizebhi.twitchtelegrambot.persistence.entity.EventSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscription, String> {

    Optional<EventSubscription> findByStream_StreamId(String streamId);

    @Modifying
    @Transactional
    void deleteAllByStream_StreamId(String streamId);

    List<EventSubscription> findByName(String name);
}
