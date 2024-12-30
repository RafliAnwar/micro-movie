package dev.dzul.user_service.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String username;
    private String phone;
    private String balance;
}
