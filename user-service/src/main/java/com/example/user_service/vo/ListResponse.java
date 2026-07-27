package com.example.user_service.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public record ListResponse<T>(
        int count,
        List<T> data
) {

    public static <T> ListResponse<T> of(List<T> data) {
        return new ListResponse<>(data.size(), data);
    }
}
