package com.sifamo.customer.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceInfoController {

    @GetMapping("/")
    public String serviceInfo() {
        return "Customer Service is running";
    }
}