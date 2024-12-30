package dev.dzul.transaction_service.transaction;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long subscriptionId;

    private LocalDateTime transaction_date;

    private LocalDateTime end_of_subscription;

    private String transaction_code;

    private String status = "PAID";
}
