package co.com.crediya.requests.sqs.sender;

import co.com.crediya.requests.model.notification.NotificationRequest;
import co.com.crediya.requests.model.notification.gateways.NotificationGateway;
import co.com.crediya.requests.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.nio.charset.StandardCharsets;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendNotification(NotificationRequest notification) {
        log.info("Preparing to send notification to SQS: {}", notification);
        return Mono.fromCallable(() -> buildRequest(notification))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent to SQS with ID: {}", response.messageId()))
                .onErrorMap(error -> {
                    log.error("Failed to send message to SQS: {}", error.getMessage());
                    return new RuntimeException("Error sending message to SQS", error);
                })
                .then();
    }

    private SendMessageRequest buildRequest(NotificationRequest notification) throws JsonProcessingException {
        String messageBody = objectMapper.writeValueAsString(notification);

        int sizeInBytes = messageBody.getBytes(StandardCharsets.UTF_8).length;

        log.info("Message size: {} bytes ({} KiB)", sizeInBytes, sizeInBytes / 1024.0);

        if (sizeInBytes > 262144) {
            log.error("Message exceeds SQS limit of 256 KiB, size: {} bytes ({} KiB)", sizeInBytes, sizeInBytes / 1024.0);
            throw new IllegalArgumentException("Message exceeds SQS limit of 256 KiB");
        }

        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(messageBody)
                .build();
    }
}
