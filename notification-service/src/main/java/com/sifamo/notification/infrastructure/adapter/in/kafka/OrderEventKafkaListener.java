package com.sifamo.notification.infrastructure.adapter.in.kafka;

import com.sifamo.notification.domain.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventKafkaListener {

    @KafkaListener(
            topics = "${app.kafka.order-events-topic}",
            groupId = "notification-service"
    )
    public void handleOrderEvent(OrderEvent event) {
        System.out.println("Received order event: " + event);
    }
}