package dev.dzul.transaction_service.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.dzul.transaction_service.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
public class User {
    private Long id;

    private String email;

//    private String password;

    private String username;

    private String phone;

    private Integer balance;
}
