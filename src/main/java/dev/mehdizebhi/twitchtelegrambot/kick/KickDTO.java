package dev.mehdizebhi.twitchtelegrambot.kick;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public class KickDTO {

    public record KickAccessTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") Long expiresIn
    ) {
    }

    public record KickChannelResponse(
            @JsonProperty("data") List<KickChannelData> data,
            @JsonProperty("message") String message
    ) {
    }

    public record KickChannelData(
            @JsonProperty("broadcaster_user_id") long broadcasterUserId,
            @JsonProperty("slug") String slug,
            @JsonProperty("channel_description") String channelDescription,
            @JsonProperty("banner_picture") String bannerPicture,
            @JsonProperty("stream") KickStream stream,
            @JsonProperty("stream_title") String streamTitle,
            @JsonProperty("category") KickCategory category
    ) {
    }

    public record KickStream(
            @JsonProperty("url") String url,
            @JsonProperty("key") String key,
            @JsonProperty("is_live") boolean isLive,
            @JsonProperty("is_mature") boolean isMature,
            @JsonProperty("language") String language,
            @JsonProperty("start_time") OffsetDateTime startTime,
            @JsonProperty("viewer_count") int viewerCount,
            @JsonProperty("thumbnail") String thumbnail
    ) {
    }

    public record KickCategory(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("thumbnail") String thumbnail
    ) {
    }

    public record KickSubscribeRequest(
            @JsonProperty("broadcaster_user_id") Long broadcasterUserId,
            @JsonProperty("events") List<KickEventInfo> events,
            @JsonProperty("method") String method
    ) {
    }

    public record KickEventInfo(
            @JsonProperty("name") String name,
            @JsonProperty("version") int version
    ) {
    }

    public record KickSubscribeData(
            @JsonProperty("error") String error,
            @JsonProperty("name") String name,
            @JsonProperty("subscription_id") String subscriptionId,
            @JsonProperty("version") int version
    ) {
    }

    public record KickSubscribeResponse(
            @JsonProperty("data") List<KickSubscribeData> data,
            @JsonProperty("message") String message
    ) {
    }

    public record KickGetSubscriptionsData(
            @JsonProperty("app_id") String appId,
            @JsonProperty("broadcaster_user_id") long broadcasterUserId,
            @JsonProperty("created_at") OffsetDateTime createdAt,
            @JsonProperty("event") String event,
            @JsonProperty("id") String id,
            @JsonProperty("method") String method,
            @JsonProperty("updated_at") OffsetDateTime updatedAt,
            @JsonProperty("version") int version
    ) {
    }

    public record KickGetSubscriptionsResponse(
            @JsonProperty("data") List<KickGetSubscriptionsData> data,
            @JsonProperty("message") String message
    ) {
    }

    public record LivestreamStatusUpdatedEvent(
            Broadcaster broadcaster,
            @JsonProperty("is_live") boolean isLive,
            String title,
            @JsonProperty("started_at") Instant startedAt,
            @JsonProperty("ended_at") Instant endedAt
    ) {
    }

    public record Broadcaster(
            @JsonProperty("is_anonymous") boolean isAnonymous,
            @JsonProperty("user_id") long userId,
            String username,
            @JsonProperty("is_verified") Boolean isVerified,
            @JsonProperty("profile_picture") String profilePicture,
            @JsonProperty("channel_slug") String channelSlug,
            String identity
    ) {
    }
}