package com.example.user_service.client;

import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class RestClientErrorHandler implements ResponseErrorHandler {

    private static final String NOT_FOUND_ENDPOINT = "엔드포인트를 찾을 수 없습니다.";

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        int statusCode = response.getStatusCode().value();
        String statusText = response.getStatusText();

        log.error("외부 API 호출 실패 - url: {}, method: {}, statusCode: {}, statusText: {}", url, method, statusCode,
                statusText);

        switch (statusCode) {
            case 400:
                return;
            case 404:
                throw new ResponseStatusException(HttpStatus.valueOf(statusCode), NOT_FOUND_ENDPOINT);
            default:
                throw new RuntimeException(statusText);
        }
    }
}
