package com.donate4life.demo.service;

import com.donate4life.demo.entity.User;
import com.donate4life.demo.exception.ResourceNotFoundException;
import com.donate4life.demo.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserService self;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, @Lazy UserService self) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.self = self;
    }

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);

        // User starts as disabled until OTP verification
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        // Send OTP email asynchronously
        self.sendOtpEmail(user.getUsername(), otp);

        return savedUser;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Donate4Life Verification Code");
            message.setText("Welcome to Donate4Life! Your verification code is: " + otp);
            mailSender.send(message);
            System.out.println("OTP Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending OTP email to " + toEmail + ": " + e.getMessage());
        }
    }

    public boolean verifyOtp(Integer userId, String otp) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isEnabled() && otp.equals(user.getOtp())) {
                user.setEnabled(true);
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public List<User> findCompatibleDonors(String requestedBloodGroup) {
        // --- DEBUG LOG 1 ---
        System.out.println("SERVICE DEBUG: Finding compatible groups for: " + requestedBloodGroup);

        List<String> compatibleGroups = getCompatibleBloodGroups(requestedBloodGroup);

        // --- DEBUG LOG 2 ---
        System.out.println("SERVICE DEBUG: Compatible groups are: " + compatibleGroups);

        if (compatibleGroups.isEmpty()) {
            return List.of(); // Return empty if no compatible groups
        }

        // ⭐ FIX: Call the new method that checks for 'enabled = true' ⭐
        List<User> donors = userRepository.findByUserTypeAndBloodGroupInAndEnabled(User.UserType.DONOR, compatibleGroups, true);

        // --- DEBUG LOG 3 ---
        System.out.println("SERVICE DEBUG: Enabled DONORs found by repository: " + donors.size());
        return donors;
    }

    private List<String> getCompatibleBloodGroups(String requestedBloodGroup) {
        if (requestedBloodGroup == null) return List.of();
        // Use toUpperCase() for robust matching
        return switch (requestedBloodGroup.toUpperCase()) {
            case "A+" -> Arrays.asList("A+", "A-", "O+", "O-");
            case "O+" -> Arrays.asList("O+", "O-");
            case "B+" -> Arrays.asList("B+", "B-", "O+", "O-");
            case "AB+" -> Arrays.asList("A+", "A-", "O+", "O-", "B+", "B-", "AB+", "AB-");
            case "A-" -> Arrays.asList("A-", "O-");
            case "O-" -> List.of("O-");
            case "B-" -> Arrays.asList("B-", "O-");
            case "AB-" -> Arrays.asList("AB-", "A-", "B-", "O-");
            default -> List.of();
        };
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     * Needs to be public so AdminController can call it.
     */
    // ⭐ FIX: Changed from 'private' to 'public' ⭐
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        // Earth's radius in kilometers
        final double R = 6371.0;

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Apply Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return R * c;
    }
}