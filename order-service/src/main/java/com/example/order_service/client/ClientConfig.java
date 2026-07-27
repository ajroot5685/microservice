package com.example.order_service.client;

import com.example.order_service.context.UserContextFilter;
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

    @Bean
    public CatalogServiceClient catalogServiceClient() {
        RestClient client = RestClient.builder()
                .baseUrl("http://CATALOG-SERVICE")
                .requestFactory(new SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(1000);
                    setReadTimeout(2000);
                }})
                .defaultStatusHandler(errorHandler)
                .requestInterceptor(loadBalancerInterceptor)
                .requestInterceptor(userIdInterceptor())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CatalogServiceClient.class);
    }

    @Bean
    public ClientHttpRequestInterceptor userIdInterceptor() {
        return (request, body, execution) -> {
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
}
