package com.donate4life.demo.controller;

import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.FileStorageService; // 1. Import FileStorageService
import com.donate4life.demo.service.RequestService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // 2. Import RequestParam
import org.springframework.web.multipart.MultipartFile; // 3. Import MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RequestController {

    private final RequestService requestService;
    private final FileStorageService fileStorageService; // 4. Declare the new service

    // 5. Inject the service in the constructor
    public RequestController(RequestService requestService, FileStorageService fileStorageService) {
        this.requestService = requestService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("request", new Request());
        return "request";
    }

    // 6. Update the method to handle the file upload
    @PostMapping("/request")
    public String processRequest(@ModelAttribute Request request,
                                 @RequestParam("document") MultipartFile document, // Get the uploaded file
                                 @AuthenticationPrincipal User currentUser,
                                 RedirectAttributes redirectAttributes) {

        if (currentUser.getUserType() != User.UserType.ACCEPTOR) {
            redirectAttributes.addFlashAttribute("error", "Only users registered as Acceptors can make a blood request.");
            return "redirect:/request";
        }

        if (document.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "A verification document is required.");
            return "redirect:/request";
        }

        // Save the uploaded file and get its unique name
        String documentUrl = fileStorageService.save(document);

        // Call the updated service method, now with the document name
        requestService.createRequest(
                currentUser.getUserId(),
                request.getRequestedBloodGroup(),
                request.getHospitalName(),
                documentUrl
        );

        redirectAttributes.addFlashAttribute("success", "Your request has been submitted for verification!");
        return "redirect:/profile";
    }
}