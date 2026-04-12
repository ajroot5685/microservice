package com.example.apigateway_service.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RouteFilter extends AbstractGatewayFilterFactory<RouteFilter.Config> {

    public RouteFilter() {
        super(Config.class);
    }

    @Data
    public static class Config {
        private String prefix;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getRawPath();

            String newPath = path.replaceFirst("^/" + config.prefix, "");
            log.info("새로운 path 갱신됨, path: {} -> new path: {}", path, newPath);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .path(newPath)
                    .header("m-request", "1st-request-header")
                    .build();

            exchange.getResponse().beforeCommit(() -> {
                exchange.getResponse().getHeaders().add("m-response", "1st-response-header");
                return Mono.empty();
            });

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }, FilterOrder.RouteOrder);
    }
}
