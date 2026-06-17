package com.ReunionSphere.User_Service.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ReunionSphere.User_Service.Entities.Subscriptions;

public interface SubscriptionsRepo extends JpaRepository<Subscriptions, String> {

    Optional <Subscriptions> findByEmail(String email);

    Optional<Subscriptions> findSubscriptionByPhoneNumber(String number);

    String  findSubscriptionIdByEmail(String email);
    String  findSubscriptionIdByPhoneNumber(String phoneNumber);
     
}
