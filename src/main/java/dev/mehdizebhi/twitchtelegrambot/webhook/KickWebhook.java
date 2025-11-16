package dev.mehdizebhi.twitchtelegrambot.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mehdizebhi.twitchtelegrambot.event.KickStreamChannelLiveEvent;
import dev.mehdizebhi.twitchtelegrambot.kick.KickDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@RestController
@RequestMapping("/kick")
public class KickWebhook {

    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private ObjectMapper objectMapper;

    private static final String KICK_PUBLIC_KEY = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq/+l1WnlRrGSolDMA+A8
            6rAhMbQGmQ2SapVcGM3zq8ANXjnhDWocMqfWcTd95btDydITa10kDvHzw9WQOqp2
            MZI7ZyrfzJuz5nhTPCiJwTwnEtWft7nV14BYRDHvlfqPUaZ+1KR4OCaO/wWIk/rQ
            L/TjY0M70gse8rlBkbo2a8rKhu69RQTRsoaf4DVhDPEeSeI5jVrRDGAMGL3cGuyY
            6CLKGdjVEM78g3JfYOvDU/RvfqD7L89TZ3iN94jrmWdGz34JNlEI5hqK8dd7C5EF
            BEbZ5jgB8s8ReQV8H+MkuffjdAj3ajDDX3DOJMIut1lBrUVD1AaSrGCKHooWoL2e
            twIDAQAB
            -----END PUBLIC KEY-----
            """;

    @PostMapping("/webhooks")
    public Mono<Void> kickWebhook(
            @RequestHeader("Kick-Event-Message-Id") String messageId,
            @RequestHeader("Kick-Event-Message-Timestamp") String timestamp,
            @RequestHeader("Kick-Event-Signature") String signature,
            @RequestHeader("Kick-Event-Type") String eventType,
            @RequestHeader(value = "Kick-Event-Subscription-Id", required = false) String subscriptionId,
            @RequestHeader(value = "Kick-Event-Version", required = false) String version,
            @RequestBody String body) {

        return Mono.fromCallable(() -> {
                    if (!verifySignature(messageId, timestamp, body, signature)) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature");
                    }
                    return true;
                })
                .flatMap(valid -> {
                    try {
                        KickDTO.LivestreamStatusUpdatedEvent event = objectMapper.readValue(body, KickDTO.LivestreamStatusUpdatedEvent.class);
                        if (event.isLive() && event.endedAt() == null) {
                            eventPublisher.publishEvent(new KickStreamChannelLiveEvent(event));
                        }
                        return Mono.empty();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON body"));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error processing webhook: " + e.getMessage());
                    return Mono.error(e);
                }).then();
    }

    // -------------------
    // Private helper
    // -------------------

    private boolean verifySignature(String messageId, String timestamp, String body, String signatureHeader) {
        try {
            String signatureData = messageId + "." + timestamp + "." + body;
            PublicKey publicKey = parsePublicKey(KICK_PUBLIC_KEY);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureHeader);
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(signatureData.getBytes());
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            System.err.println("Signature verification failed: " + e.getMessage());
            return false;
        }
    }

    private PublicKey parsePublicKey(String publicKeyPEM) throws Exception {
        String publicKeyPEMFormatted = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEMFormatted);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}