package dev.dzul.subscription_service.subscription;

import dev.dzul.subscription_service.utils.ResponseFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subs")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping ("")
    public ResponseEntity<ResponseFormatter<List<ResponseDTO>>> getAllSubscriptions() {
        try{
            List<ResponseDTO> subs = subscriptionService.getAllSubscriptions();
            return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Subscriptions fetched successfully", subs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormatter<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while fetching subscriptions", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseFormatter<ResponseDTO>> getSubscriptionById(@PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.getSubscriptionById(id);
            if (subscription != null) {
                ResponseDTO subscriptionDTO = subscriptionService.mapEntityToDto(subscription);
                return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Subscription fetched successfully", subscriptionDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseFormatter<>(HttpStatus.NOT_FOUND.value(), "Subscription not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormatter<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while fetching the subscription", null));
        }
    }

    @PostMapping("")
    public ResponseEntity<ResponseFormatter<ResponseDTO>> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        try {
                ResponseDTO subscription = subscriptionService.createSubscription(subscriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseFormatter<>(HttpStatus.CREATED.value(), "Subscription created successfully", subscription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormatter<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while creating the subscription "+e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseFormatter<ResponseDTO>> updateSubscription(
            @PathVariable Long id, @RequestBody SubscriptionDTO subscriptionDTO) {
        try {
            ResponseDTO subscription = subscriptionService.updateSubscription(id, subscriptionDTO);
            if (subscription != null) {
                return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Subscription updated successfully", subscription));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseFormatter<>(HttpStatus.NOT_FOUND.value(), "Subscription not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormatter<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while updating the subscription "+ e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseFormatter<Void>> deleteSubscription(@PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok(new ResponseFormatter<>(HttpStatus.OK.value(), "Subscription deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormatter<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Subscription with id: "+id+ " is not found", null));
        }
    }




}
