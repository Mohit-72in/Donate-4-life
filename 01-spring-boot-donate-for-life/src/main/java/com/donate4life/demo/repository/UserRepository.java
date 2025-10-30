package com.donate4life.demo.repository;

import com.donate4life.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    // ⭐ NEW: Find all users who are DONORs and have a specific blood group
    List<User> findByUserTypeAndBloodGroup(User.UserType userType, String bloodGroup);


    // This is the required method. Note the "In" at the end.
    List<User> findByUserTypeAndBloodGroupIn(User.UserType userType, List<String> bloodGroups);

    // ⭐ ADD THIS METHOD ⭐
    // This finds only donors who have verified their OTP (enabled = true)
    List<User> findByUserTypeAndBloodGroupInAndEnabled(User.UserType userType, List<String> bloodGroups, boolean enabled);
}