package com.micnusz.gateway.Security;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class ObservabilityFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityFilter.class);
    private final MeterRegistry meterRegistry;

    public ObservabilityFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String routeId = exchange.getAttribute(GATEWAY_ROUTE_ATTR) != null ?
                ((Route) Objects.requireNonNull(exchange.getAttribute(GATEWAY_ROUTE_ATTR))).getId() : "unknown";

        Timer.Sample sample = Timer.start(meterRegistry);

        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    sample.stop(Timer.builder("gateway.request.duration")
                            .tag("route", routeId)
                            .tag("status", String.valueOf(exchange.getResponse().getStatusCode().value()))
                            .register(meterRegistry));
                })
                .doOnError(throwable -> {
                    log.error("Gateway error for route {}: {}", routeId, throwable.getMessage());
                    sample.stop(Timer.builder("gateway.request.duration")
                            .tag("route", routeId)
                            .tag("status", "error")
                            .register(meterRegistry));
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}