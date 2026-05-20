package com.sifamo.notification.infrastructure.adapter.in.kafka;

import com.sifamo.notification.application.port.in.HandleOrderEventUseCase;
import com.sifamo.notification.domain.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventKafkaListener {
	
	private final HandleOrderEventUseCase handleOrderEventUseCase;
	
	public OrderEventKafkaListener(HandleOrderEventUseCase handleOrderEventUseCase) {
        this.handleOrderEventUseCase = handleOrderEventUseCase;
    }


    @KafkaListener(
            topics = "${app.kafka.order-events-topic}",
            groupId = "notification-service-v2"
    )
    public void handleOrderEvent(OrderEvent event) {
        System.out.println("Received order event: " + event);
        handleOrderEventUseCase.handle(event);
    }
}