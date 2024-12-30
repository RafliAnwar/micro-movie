package dev.dzul.transaction_service.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDTO {
    @NotNull(message = "User ID cannot be null")
    private Long id_user;
    @NotNull(message = "Subscription ID cannot be null")
    private Long id_subscription;

    private LocalDateTime transaction_date;
    private LocalDateTime end_of_subscription;
    private String transaction_code;
    private String status;
}
