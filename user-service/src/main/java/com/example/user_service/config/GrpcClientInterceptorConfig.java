package com.example.user_service.config;

import com.example.user_service.context.UserContextFilter;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientInterceptorConfig {

    private static final Metadata.Key<String> USER_ID_KEY = Metadata.Key.of("userId", Metadata.ASCII_STRING_MARSHALLER);

    @GrpcGlobalClientInterceptor
    public ClientInterceptor userIdInterceptor() {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                                       CallOptions callOptions, Channel channel) {
                return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                        channel.newCall(methodDescriptor, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        if (UserContextFilter.USER_ID.isBound()) {
                            String userId = UserContextFilter.USER_ID.get();
                            if (userId != null && !userId.isEmpty()) {
                                headers.put(USER_ID_KEY, userId);
                            }
                        }
                        super.start(responseListener, headers);
                    }
                };
            }
        };
    }
}
