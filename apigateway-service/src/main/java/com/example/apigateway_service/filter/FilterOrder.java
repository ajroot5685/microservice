package com.example.apigateway_service.filter;

import lombok.experimental.UtilityClass;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;

@UtilityClass
public class FilterOrder {

    public static final int LoggingOrder = OrderedGatewayFilter.HIGHEST_PRECEDENCE;
    public static final int RouteOrder = OrderedGatewayFilter.HIGHEST_PRECEDENCE + 1;
    public static final int AuthorizationOrder = OrderedGatewayFilter.HIGHEST_PRECEDENCE + 2;
}
