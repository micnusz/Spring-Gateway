package com.micnusz.gateway.Controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<Map<String, String>> userServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(Map.of(
                "status", "503",
                "service", "user-service",
                "message", "User service is currently unavailable. Please try again later."
        ));
    }

    @GetMapping("/role-service")
    public Mono<Map<String, String>> roleServiceFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(Map.of(
                "status", "503",
                "service", "role-service",
                "message", "Role service is currently unavailable. Please try again later."
        ));
    }

}
