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

        User savedUser = userRepository.save(user);

        self.sendOtpEmail(user.getUsername(), otp);

        return savedUser;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Donate4Life Verification Code");
        message.setText("Welcome to Donate4Life! Your verification code is: " + otp);
        mailSender.send(message);
    }

    public boolean verifyOtp(Integer userId, String otp) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (otp.equals(user.getOtp())) {
                user.setEnabled(true);
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public List<User> findCompatibleDonors(String requestedBloodGroup) {
        List<String> compatibleGroups = getCompatibleBloodGroups(requestedBloodGroup);
        return userRepository.findByUserTypeAndBloodGroupIn(User.UserType.DONOR, compatibleGroups);
    }

    private List<String> getCompatibleBloodGroups(String requestedBloodGroup) {
        return switch (requestedBloodGroup) {
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
}