package com.example.order_service.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcServerInterceptorConfig {

    public static final Metadata.Key<String> USER_ID_KEY = Metadata.Key.of("userId", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> USER_ID_CONTEXT_KEY = Context.key("userId");

    @GrpcGlobalServerInterceptor
    public ServerInterceptor userIdServerInterceptor() {
        return new ServerInterceptor() {
            @Override
            public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata,
                                                              ServerCallHandler<ReqT, RespT> next) {
                String userId = metadata.get(USER_ID_KEY);
                if (userId != null && !userId.isEmpty()) {
                    Context context = Context.current().withValue(USER_ID_CONTEXT_KEY, userId);
                    return Contexts.interceptCall(context, serverCall, metadata, next);
                }

                log.error("gRPC 요청 헤더의 userId 식별 실패");
                return next.startCall(serverCall, metadata);
            }
        };
    }
}
