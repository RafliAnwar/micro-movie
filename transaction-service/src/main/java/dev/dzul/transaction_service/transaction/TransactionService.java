package dev.dzul.transaction_service.transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dzul.transaction_service.utils.ResponseFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<ResponseDTO> getAllTransactions() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream().map(transaction -> {
            ResponseDTO responseDTO = mapEntityToDto(transaction);

            // Fetch User from User Service
            String userApiUrl = "http://localhost:8082/api/user/" + transaction.getUserId();
            try
            {
                ResponseEntity<String> userResponse = restTemplate.exchange(
                        userApiUrl,
                        HttpMethod.GET,
                        null,
                        String.class
                );

                ResponseFormatter<User> userApiResponse = objectMapper.readValue(
                        userResponse.getBody(),
                        new TypeReference<ResponseFormatter<User>>() {}
                );

                if (userApiResponse != null && userApiResponse.getData() != null) {
                    responseDTO.setUsername(userApiResponse.getData().getUsername());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch user data: "+e.getMessage());
            }

            //Fetch Subscription from Subscription Service
            String subscriptionApiUrl = "http://localhost:8081/api/subs/" + transaction.getSubscriptionId();
            try{
                ResponseEntity<String> subscriptionResponse = restTemplate.exchange(
                        subscriptionApiUrl,
                        HttpMethod.GET,
                        null,
                        String.class
                );

                ResponseFormatter<Subscription> subscriptionApiResponse = objectMapper.readValue(
                        subscriptionResponse.getBody(),
                        new TypeReference<ResponseFormatter<Subscription>>() {}
                );

                if (subscriptionApiResponse != null && subscriptionApiResponse.getData() != null) {
                    responseDTO.setSubscription(subscriptionApiResponse.getData());
                }
            }catch (Exception e){
                throw new RuntimeException("Failed to fetch subscription data: "+e.getMessage());
            }

            return responseDTO;
        }).toList();
    }

    public List<ResponseDTO> getAllTransactionsByUser(Long idUser) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        List<Transaction> transactions = transactionRepository.findAllByUserId(idUser);

        return transactions.stream().map(transaction -> {
            ResponseDTO responseDTO = mapEntityToDto(transaction);

            String userApiUrl = "http://localhost:8082/api/user/" + transaction.getUserId();
            try{
                ResponseEntity<String> userResponse = restTemplate.exchange(
                        userApiUrl,
                        HttpMethod.GET,
                        null,
                        String.class
                );

                ResponseFormatter<User> userApiResponse = objectMapper.readValue(
                        userResponse.getBody(),
                        new TypeReference<ResponseFormatter<User>>() {}
                );

                if (userApiResponse != null && userApiResponse.getData() != null) {
                    responseDTO.setUsername(userApiResponse.getData().getUsername());
                }
            }
            catch (Exception e){
                throw new RuntimeException("Failed to fetch user data: "+e.getMessage());
            }

            String subscriptionApiUrl = "http://localhost:8081/api/subs/" + transaction.getSubscriptionId();
            try{
                ResponseEntity<String> subscriptionResponse = restTemplate.exchange(
                        subscriptionApiUrl,
                        HttpMethod.GET,
                        null,
                        String.class
                );

                ResponseFormatter<Subscription> subscriptionApiResponse = objectMapper.readValue(
                        subscriptionResponse.getBody(),
                        new TypeReference<ResponseFormatter<Subscription>>() {}
                );

                if (subscriptionApiResponse != null && subscriptionApiResponse.getData() != null) {
                    responseDTO.setSubscription(subscriptionApiResponse.getData());
                }
            }
            catch (Exception e){
                throw new RuntimeException("Failed to fetch subscription data: "+e.getMessage());
            }

            return responseDTO;
        }).toList();
    }

    public ResponseDTO createTransaction(TransactionDTO transactionDTO) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Fetch User from User Service
        String userApiUrl = "http://localhost:8082/api/user/" + transactionDTO.getId_user();
        ResponseEntity<String> rawResponse = restTemplate.exchange(
                userApiUrl,
                HttpMethod.GET,
                null,
                String.class
        );
        System.out.println("Raw Response: " + rawResponse.getBody());

        ResponseFormatter<User> apiResponse;
        try {
            apiResponse = objectMapper.readValue(
                    rawResponse.getBody(),
                    new TypeReference<ResponseFormatter<User>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user response: " + e.getMessage());
        }

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new IllegalArgumentException("User not found with ID: " + transactionDTO.getId_user());
        }

        User user = apiResponse.getData();
        System.out.println("User fetched: " + user);
        System.out.println("User balance: " + user.getBalance());

        // Fetch Subscription from Subscription Service
        String subscriptionApiUrl = "http://localhost:8081/api/subs/" + transactionDTO.getId_subscription();
        ResponseEntity<String> rawSubscriptionResponse = restTemplate.exchange(
                subscriptionApiUrl,
                HttpMethod.GET,
                null,
                String.class
        );
        System.out.println("Raw Subscription Response: " + rawSubscriptionResponse.getBody());

        ResponseFormatter<Subscription> subscriptionApiResponse;
        try {
            subscriptionApiResponse = objectMapper.readValue(
                    rawSubscriptionResponse.getBody(),
                    new TypeReference<ResponseFormatter<Subscription>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse subscription response: " + e.getMessage());
        }

        if (subscriptionApiResponse == null || subscriptionApiResponse.getData() == null) {
            throw new IllegalArgumentException("Subscription not found with ID: " + transactionDTO.getId_subscription());
        }

        Subscription subscription = subscriptionApiResponse.getData();
        System.out.println("Fetched Subscription: " + subscription);

        if (subscription.getPrice() == null) {
            throw new IllegalArgumentException("Subscription price is missing for ID: " + transactionDTO.getId_subscription());
        }

        if (user.getBalance() < subscription.getPrice()) {
            throw new IllegalArgumentException("Insufficient balance for the transaction.");
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setSubscriptionId(subscription.getId());
        transaction.setTransaction_date(LocalDateTime.now());
        transaction.setEnd_of_subscription(LocalDateTime.now().plusDays(subscription.getDuration()));
        transaction.setTransaction_code(generateTransactionCode());
        transaction.setStatus("PAID");

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update User Balance via User Service
        BalanceRequestDTO balanceRequest = new BalanceRequestDTO();
        balanceRequest.setAmount(-subscription.getPrice());
        restTemplate.put(userApiUrl + "/balance", balanceRequest);

        // Map to ResponseDTO
        ResponseDTO responseDTO = mapEntityToDto(savedTransaction);
        responseDTO.setUsername(user.getUsername());
        responseDTO.setSubscription(subscription);

        return responseDTO;
    }


    private String generateTransactionCode() {
        int length = 20;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder transactionCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            transactionCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return transactionCode.toString();
    }

    private ResponseDTO mapEntityToDto(Transaction transaction) {
        ResponseDTO dto = new ResponseDTO();
        BeanUtils.copyProperties(transaction, dto);
        dto.setTransaction_code(transaction.getTransaction_code());
        return dto;
    }

    //    public ResponseDTO createTransaction(TransactionDTO transactionDTO) {
//        // Fetch User from User Service
//        RestTemplate restTemplate = new RestTemplate();
//        String userApiUrl = "http://localhost:8082/api/user/" + transactionDTO.getId_user();
//        User user = restTemplate.getForObject(userApiUrl, User.class);
//
//        if (user == null) {
//            throw new IllegalArgumentException("User not found with ID: " + transactionDTO.getId_user());
//        }
//
//        System.out.println("User fetched: " + user);
//        System.out.println("User balance: " + user.getBalance());
//
//        // Fetch Subscription from Subscription Service
//        String subscriptionApiUrl = "http://localhost:8081/api/subs/" + transactionDTO.getId_subscription();
//        Subscription subscription = restTemplate.getForObject(subscriptionApiUrl, Subscription.class);
//
//        if (subscription == null) {
//            throw new IllegalArgumentException("Subscription not found with ID: " + transactionDTO.getId_subscription());
//        }
//
//        // Check User Balance
//        if (user.getBalance() < subscription.getPrice()) {
//            throw new IllegalArgumentException("Insufficient balance for the transaction.");
//        }
//
//        // Create Transaction
//        Transaction transaction = new Transaction();
//        transaction.setUserId(user.getId());
//        transaction.setSubscriptionId(subscription.getId());
//        transaction.setTransaction_date(LocalDateTime.now());
//        transaction.setEnd_of_subscription(LocalDateTime.now().plusDays(subscription.getDuration()));
//        transaction.setTransaction_code(generateTransactionCode());
//        transaction.setStatus("PAID");
//
//        // Save Transaction
//        Transaction savedTransaction = transactionRepository.save(transaction);
//
//        // Update User Balance via User Service
//        user.setBalance(user.getBalance() - subscription.getPrice());
//        restTemplate.put(userApiUrl, user);
//
//        // Map to ResponseDTO
//        ResponseDTO responseDTO = mapEntityToDto(savedTransaction);
//        responseDTO.setUsername(user.getUsername());
//        responseDTO.setSubscription(subscription);
//
//        return responseDTO;
//    }

}
