package dev.dzul.subscription_service.subscription;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer price;

    private Integer duration;

    private Boolean is_4k;

}
