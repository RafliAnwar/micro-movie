package dev.dzul.user_service.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

//    public List<ResponseDTO> findAllUsersWithTransactions() { //NEW
//        List<User> users = userRepository.findAll();
//
//        List<ResponseDTO> userWithTransactionDTOs = new ArrayList<>();
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        for (User user : users) {
//            ResponseDTO responseDTO = new ResponseDTO();
//            responseDTO.setId(user.getId());
//            responseDTO.setEmail(user.getEmail());
//            responseDTO.setUsername(user.getUsername());
//            responseDTO.setBalance(user.getBalance());
//
//            List<TransactionByIdDTO> transactions = new ArrayList<>();
//            try {
//                TransactionByIdDTO[] fetchedTransactions = restTemplate.getForObject(
//                        "http://localhost:8083/api/transactions/" + user.getId(), TransactionByIdDTO[].class);
//
//                if (fetchedTransactions != null) {
//                    transactions = List.of(fetchedTransactions);
//                }
//            } catch (Exception e) {
//                System.err.println("Error fetching transactions for user " + user.getId() + ": " + e.getMessage());
//            }
//
//            responseDTO.setTransaction(transactions);
//
//            userWithTransactionDTOs.add(responseDTO);
//        }
//
//        return userWithTransactionDTOs;
//    }

    public List<ResponseDTO> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc().stream().map(this::mapEntityToDto).toList();
    }

    public ResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return mapEntityToDto(user);
    }

    public ResponseDTO registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        user.setBalance(0);

        User savedUser = userRepository.save(user);

        ResponseDTO responseDTO = new ResponseDTO();
        BeanUtils.copyProperties(savedUser, responseDTO);
        return responseDTO;
    }

    // Method to add balance
    public ResponseDTO addBalance(Long userId, Integer amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        if (user.getBalance() + amount < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setBalance(user.getBalance() + amount);
        User updatedUser = userRepository.save(user);
        // Map updated user to ResponseDTO
        ResponseDTO responseDTO = new ResponseDTO();
        BeanUtils.copyProperties(updatedUser, responseDTO);
        return responseDTO;
    }

    public User mapDtoToEntity(UserDTO userDTO, User user) {
        BeanUtils.copyProperties(userDTO, user, "id");
        return user;
    }

    public ResponseDTO mapEntityToDto(User user) {
        ResponseDTO dto = new ResponseDTO();
        BeanUtils.copyProperties(user, dto);

        return dto;
//        List<TransactionByIdDTO> transactionDTOs = user.getTransactions().stream().map(transaction -> {
//            TransactionByIdDTO transactionDTO = new TransactionByIdDTO();
//            transactionDTO.setId(transaction.getId());
//            transactionDTO.setTransactionCode(transaction.getTransaction_code());
//            transactionDTO.setStatus(transaction.getStatus());
//            transactionDTO.setTransactionDate(transaction.getTransaction_date());
//            transactionDTO.setEndOfSubscription(transaction.getEnd_of_subscription());
//
//            return transactionDTO;
//        }).toList();
//
//        dto.setTransaction(transactionDTOs);
    }



}
