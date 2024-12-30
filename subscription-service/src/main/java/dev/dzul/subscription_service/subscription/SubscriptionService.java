package dev.dzul.subscription_service.subscription;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    @Autowired
    SubscriptionRepository subscriptionRepository;

    public List<ResponseDTO> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAllByOrderByIdAsc();
        return subscriptions.stream().map(this::mapEntityToDto).toList();
    }

    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id).orElse(null);
    }

    public ResponseDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        Subscription subscription = new Subscription();
        mapDtoToEntity(subscriptionDTO, subscription);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return mapEntityToDto(savedSubscription);
    }

    public ResponseDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        Subscription subscription = getSubscriptionById(id);
        if (subscription != null) {
            var updatedSubscription = mapDtoToEntity(subscriptionDTO, subscription);
            Subscription savedSubscription = subscriptionRepository.save(updatedSubscription);
            return mapEntityToDto(savedSubscription);
        }else{
            throw new RuntimeException("Subscription with id " + id + " not found.");
        }

    }

    public void deleteSubscription(Long id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("Subscription with id: "+id+ "is not found");
        }
    }


    public Subscription mapDtoToEntity(SubscriptionDTO dto, Subscription subscription) {
        BeanUtils.copyProperties(dto, subscription, "id");
        return subscription;
    }

    public ResponseDTO mapEntityToDto(Subscription subscription) {
        ResponseDTO dto = new ResponseDTO();
        BeanUtils.copyProperties(subscription, dto);
        return dto;
    }

}
