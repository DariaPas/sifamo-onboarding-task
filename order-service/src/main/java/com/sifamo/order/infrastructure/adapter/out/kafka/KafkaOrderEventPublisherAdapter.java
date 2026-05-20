package com.sifamo.order.infrastructure.adapter.out.kafka;

import com.sifamo.order.application.port.out.OrderEventPublisherPort;
import com.sifamo.order.domain.event.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String topic;

    public KafkaOrderEventPublisherAdapter(
            KafkaTemplate<String, OrderEvent> kafkaTemplate,
            @Value("${app.kafka.order-events-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(OrderEvent event) {
        kafkaTemplate.send(topic, event.orderId().toString(), event);
    }
}
