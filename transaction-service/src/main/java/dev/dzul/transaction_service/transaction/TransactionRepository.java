package dev.dzul.transaction_service.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository <Transaction, Long> {
    List<Transaction> findAllByOrderByIdAsc();

    List<Transaction> findAllByUserId(Long userId);

}
