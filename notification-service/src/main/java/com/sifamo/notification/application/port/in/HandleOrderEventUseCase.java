package com.sifamo.notification.application.port.in;

import com.sifamo.notification.domain.event.OrderEvent;

public interface HandleOrderEventUseCase {

    void handle(OrderEvent event);
}