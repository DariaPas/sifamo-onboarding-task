package com.sifamo.order.application.port.out;

import com.sifamo.order.domain.event.OrderEvent;

public interface OrderEventPublisherPort {
	
	void publish(OrderEvent event);

}
