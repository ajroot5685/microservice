package com.example.order_service.config;

import io.micrometer.observation.ObservationPredicate;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.otel.bridge.EventPublishingContextWrapper;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.micrometer.tracing.otel.bridge.Slf4JEventListener;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * 원래 이런 설정들 없이 자동으로 설정되야 하는것이 정상이나, 최신 스프링 버전에서는 제대로 업데이트가 되지 않은것인지 제대로 동작하지 않음. 수동 설정으로 억지로 traceid/spanid로 로그 시각화
 * 하는데까지는 성공했으나, 한 요청에서 여러 서비스 간의 traceid를 하나로 묶는 것에 실패함.
 */
@Configuration
public class TracingConfig {

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/actuator",
            "/eureka",
            "/favicon.ico"
    );

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public Slf4JEventListener slf4jEventListener() {
        return new Slf4JEventListener();
    }

    @Bean
    public OtelCurrentTraceContext otelCurrentTraceContext(Slf4JEventListener slf4JEventListener) {
        ContextStorage.addWrapper(new EventPublishingContextWrapper(slf4JEventListener::onEvent));
        return new OtelCurrentTraceContext();
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider(
            @Value("${management.otlp.tracing.endpoint}") String endpoint) {

        Resource resource = Resource.getDefault().merge(
                Resource.create(Attributes.builder().put("service.name", serviceName).build())
        );

        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
                .setEndpoint(endpoint)
                .build();

        return SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(resource)
                .setSampler(Sampler.alwaysOn())
                .build();
    }

    @Bean
    public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider) {
        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();

        GlobalOpenTelemetry.set(openTelemetry);

        return openTelemetry;
    }

    @Bean
    public Tracer micrometerTracer(OpenTelemetry openTelemetry, OtelCurrentTraceContext otelCurrentTraceContext,
                                   Slf4JEventListener slf4jEventListener) {
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetry.getTracer(serviceName);
        return new OtelTracer(
                otelTracer,
                otelCurrentTraceContext,
                slf4jEventListener::onEvent,
                new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(), Collections.emptyList())
        );
    }

    @Bean
    public DefaultTracingObservationHandler defaultTracingObservationHandler(Tracer tracer) {
        return new DefaultTracingObservationHandler(tracer);
    }

    @Bean
    public ObservationPredicate skipObservations() {
        return (name, context) -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                HttpServletRequest request = serverContext.getCarrier();
                if (request != null && request.getRequestURI() != null) {
                    String uri = request.getRequestURI();
                    if (EXCLUDE_PATHS.stream().anyMatch(uri::startsWith)) {
                        return false;
                    }
                }
            }

            if ("http.client.requests".equals(name)
                    && context instanceof ClientRequestObservationContext clientContext) {
                Object carrier = clientContext.getCarrier();
                if (carrier instanceof HttpRequest httpRequest) {
                    String url = httpRequest.getURI().toString();
                    if (url.contains("/eureka")) {
                        return false;
                    }
                }
            }

            return true;
        };
    }
}
