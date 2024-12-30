package dev.dzul.subscription_service.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;

    private String name;

    private Integer price;

    private Integer duration;

    private Boolean is_4k;
}
