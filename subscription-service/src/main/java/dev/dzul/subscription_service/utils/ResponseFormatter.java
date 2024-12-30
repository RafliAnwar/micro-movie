package dev.dzul.subscription_service.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseFormatter <T> {
    private Integer status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseFormatter(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
