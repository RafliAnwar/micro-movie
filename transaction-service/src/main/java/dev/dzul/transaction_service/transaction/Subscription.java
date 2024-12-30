package dev.dzul.transaction_service.transaction;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Subscription {
    private Long id;

    private String name;

    private Integer price;

    private Integer duration;

    private Boolean is_4k;

}
