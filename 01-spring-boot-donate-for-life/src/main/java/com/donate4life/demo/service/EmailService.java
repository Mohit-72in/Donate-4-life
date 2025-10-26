package com.donate4life.demo.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate; // Add this import

@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final String senderEmail;

    public EmailService(
            @Value("${sendgrid.api.key}") String apiKey,
            @Value("${app.sender.email}") String senderEmail) {
        this.sendGrid = new SendGrid(apiKey);
        this.senderEmail = senderEmail;
    }

    public void sendDonationRequestEmail(String donorEmail, String donorName, String acceptorName, String hospital, String acceptorPhone) {
        String subject = "Urgent: A Life Needs Your Help - Blood Donation Request";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Thank you for being a hero with Donate4Life. There is an urgent blood request that matches your profile.\n\n" +
                        "Patient Name: %s\n" +
                        "Location: %s\n" +
                        "Contact Number: %s\n\n" +
                        "Please contact them if you are available and able to donate. Your generosity can save a life.\n\n" +
                        "Warmly,\nThe Donate4Life Team",
                donorName, acceptorName, hospital, acceptorPhone
        );
        sendEmail(donorEmail, subject, body);
    }

    //  NEW: This was the missing method
    public void sendDonationRejectionEmail(String donorEmail, String donorName, LocalDate donationDate, String reason) {
        String subject = "Update on your recent donation submission - Donate4Life";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Thank you for submitting your recent donation on %s.\n\n" +
                        "After review, we were unable to approve this submission at this time.\n" +
                        "Reason: %s\n\n" +
                        "If you believe this is a mistake or have further questions, please don't hesitate to contact our support team.\n\n" +
                        "Thank you for your understanding and continued support.\n\n" +
                        "Warmly,\nThe Donate4Life Team",
                donorName, donationDate.toString(), reason
        );
        sendEmail(donorEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(senderEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            System.out.println("Email sent! Status Code: " + response.getStatusCode());
        } catch (IOException ex) {
            System.err.println("Error sending email: " + ex.getMessage());
        }
    }
}