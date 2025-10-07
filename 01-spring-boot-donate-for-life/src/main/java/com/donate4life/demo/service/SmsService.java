//package com.donate4life.demo.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//
//    @Value("${twilio.account.sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth.token}")
//    private String authToken;
//
//    @Value("${twilio.phone.number}")
//    private String twilioPhoneNumber;
//
//    // This method runs once, right after the service is created, to initialize the Twilio client.
//    @PostConstruct
//    public void initTwilio() {
//        Twilio.init(accountSid, authToken);
//    }
//
//    public void sendSms(String toPhoneNumber, String messageBody) {
//        try {
//            // Ensure the phone number is in E.164 format (e.g., +919876543210)
//            if (!toPhoneNumber.startsWith("+")) {
//                // Assuming Indian numbers if no country code is provided
//                toPhoneNumber = "+91" + toPhoneNumber;
//            }
//
//            Message.creator(
//                    new PhoneNumber(toPhoneNumber),
//                    new PhoneNumber(twilioPhoneNumber),
//                    messageBody
//            ).create();
//
//            System.out.println("OTP SMS sent to: " + toPhoneNumber);
//
//        } catch (Exception e) {
//            System.err.println("Could not send SMS. Error: " + e.getMessage());
//            // In a real-world app, you would have more robust error logging here.
//        }
//    }
//}