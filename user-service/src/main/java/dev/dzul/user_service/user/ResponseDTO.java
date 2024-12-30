package dev.dzul.user_service.user;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String phone;
    private Integer balance;
//    private List<TransactionByIdDTO> transaction;
}

