package dev.dzul.transaction_service.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseFormatter <T> {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // Default constructor (required for Jackson)
    public ResponseFormatter() {}

    @JsonCreator
    public ResponseFormatter(
            @JsonProperty("status") Integer status,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data
    ) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
