package com.example.user_service.config;

import com.example.user_service.client.OrderServiceClient;
import com.example.user_service.client.RestClientErrorHandler;
import com.example.user_service.context.UserContextFilter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final RestClientErrorHandler errorHandler;
    private final LoadBalancerInterceptor loadBalancerInterceptor;
    private final Tracer tracer;

    @Bean
    public OrderServiceClient orderServiceClient() {
        RestClient client = RestClient.builder()
                .baseUrl("http://ORDER-SERVICE")
                .requestFactory(new SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(1000);
                    setReadTimeout(2000);
                }})
                .defaultStatusHandler(errorHandler)
                .requestInterceptor(loadBalancerInterceptor)
                .requestInterceptor(tracingInterceptor())
                .requestInterceptor(userIdInterceptor())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OrderServiceClient.class);
    }

    @Bean
    public ClientHttpRequestInterceptor userIdInterceptor() {
        return (request, body, execution) -> {

            log.info("헤더: {}", request.getHeaders());
            if (UserContextFilter.USER_ID.isBound()) {
                String userId = UserContextFilter.USER_ID.get();
                if (userId != null && !userId.isEmpty()) {
                    request.getHeaders().add("userId", userId);
                    return execution.execute(request, body);
                }
            }

            log.warn("허용되지 않은 플로우(user id 컨텍스트 필요)");
            return execution.execute(request, body);
        };
    }

    @Bean
    public ClientHttpRequestInterceptor tracingInterceptor() {
        return (request, body, execution) -> {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null && currentSpan.context() != null) {
                String traceId = currentSpan.context().traceId();
                String spanId = currentSpan.context().spanId();

                String traceparent = String.format("00-%s-%s-01", traceId, spanId);

                request.getHeaders().add("traceparent", traceparent);
                log.info("[Tracing 수동 주입 성공] traceparent: {}", traceparent);
            } else {
                log.warn("[Tracing 실패] 현재 스레드에 활성화된 Trace Span이 없습니다.");
            }
            return execution.execute(request, body);
        };
    }
}
