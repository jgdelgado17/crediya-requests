package co.com.crediya.requests.model.notification.gateways;

import co.com.crediya.requests.model.notification.NotificationRequest;
import reactor.core.publisher.Mono;

public interface NotificationGateway {
    Mono<Void> sendNotification(NotificationRequest notification);
}
