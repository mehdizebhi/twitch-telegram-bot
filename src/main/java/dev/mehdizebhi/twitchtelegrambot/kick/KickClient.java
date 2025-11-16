package dev.mehdizebhi.twitchtelegrambot.kick;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

public interface KickClient {

    @GetExchange(url = "/channels")
    KickDTO.KickChannelResponse getChannelBySlug(@RequestParam("slug") String slug, @RequestHeader("Authorization") String bearerToken);

    @GetExchange(url = "/channels")
    KickDTO.KickChannelResponse getChannelByUserId(@RequestParam("broadcaster_user_id") Long broadcasterUserId, @RequestHeader("Authorization") String bearerToken);

    @GetExchange(url = "/events/subscriptions")
    KickDTO.KickGetSubscriptionsResponse getEventSubscriptions(@RequestParam("broadcaster_user_id") Long broadcasterUserId, @RequestHeader("Authorization") String bearerToken);

    @PostExchange(url = "/events/subscriptions", contentType = "application/json")
    KickDTO.KickSubscribeResponse subscribe(@RequestBody KickDTO.KickSubscribeRequest request, @RequestHeader("Authorization") String bearerToken);

    @DeleteExchange(url = "/events/subscriptions")
    void unsubscribe(@RequestParam("id") List<String> subscriptionIds, @RequestHeader("Authorization") String bearerToken);
}
