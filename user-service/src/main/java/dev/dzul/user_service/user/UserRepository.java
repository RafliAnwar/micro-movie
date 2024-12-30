package dev.dzul.user_service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {
    List<User> findAllByOrderByIdAsc();

    Optional <User> findByEmail(String email);
}
