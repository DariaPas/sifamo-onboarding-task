package com.sifamo.notification.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceInfoController {

    @GetMapping("/")
    public String serviceInfo() {
        return "Notification Service is running";
    }
}