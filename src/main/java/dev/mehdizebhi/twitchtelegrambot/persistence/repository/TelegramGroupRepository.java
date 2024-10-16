package dev.mehdizebhi.twitchtelegrambot.persistence.repository;

import dev.mehdizebhi.twitchtelegrambot.persistence.entity.TelegramGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramGroupRepository extends JpaRepository<TelegramGroup, Long> {

    Optional<TelegramGroup> findByChatId(Long chatId);

}
