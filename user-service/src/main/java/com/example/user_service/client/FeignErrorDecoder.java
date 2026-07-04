package com.example.user_service.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private static final String NOT_FOUND_ENDPOINT = "엔드포인트를 찾을 수 없습니다.";

    @Override
    public Exception decode(String s, Response response) {
        switch (response.status()) {
            case 400:
                break;
            case 404:
                return new ResponseStatusException(HttpStatus.valueOf(response.status()), NOT_FOUND_ENDPOINT);
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}
