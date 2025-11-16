package dev.mehdizebhi.twitchtelegrambot.persistence.repository;

import dev.mehdizebhi.twitchtelegrambot.persistence.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, String> {

    Optional<Stream> findByStreamId(String twitchId);
}
