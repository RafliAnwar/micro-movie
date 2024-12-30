package dev.dzul.transaction_service.transaction;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BalanceRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 214782385698596L;

    private Integer amount;
}
