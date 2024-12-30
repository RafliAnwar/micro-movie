package dev.dzul.transaction_service.transaction;

import dev.dzul.transaction_service.utils.ResponseFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ResponseFormatter<List<ResponseDTO>>> getAllTransactions() {
        List<ResponseDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Transactions fetched successfully", transactions));
    } // ResponseEntity<ResponseFormatter<List<Transaction>>>

    @GetMapping("/{id}")
    public ResponseEntity<ResponseFormatter<List<ResponseDTO>>> getTransactionsByUserId(@PathVariable Long id) {
        List<ResponseDTO> transactions = transactionService.getAllTransactionsByUser(id); // Mengambil List<ResponseDTO>
        return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Transactions fetched successfully", transactions));
    }


    @PostMapping
    public ResponseEntity<ResponseFormatter<ResponseDTO>> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        ResponseDTO responseDTO = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseFormatter<>(HttpStatus.CREATED.value(), "Transaction created successfully", responseDTO));
    }
}
