package com.donate4life.demo.controller;

import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.FileStorageService;
import com.donate4life.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Value; // ⭐ IMPORT
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RequestController {

    private final RequestService requestService;
    private final FileStorageService fileStorageService;

    // ⭐ ADD THIS
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public RequestController(RequestService requestService, FileStorageService fileStorageService) {
        this.requestService = requestService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("request", new Request());
        model.addAttribute("googleMapsApiKey", googleMapsApiKey); // ⭐ ADD THIS LINE
        return "request";
    }

    @PostMapping("/request")
    public String processRequest(@ModelAttribute Request request,
                                 @RequestParam("document") MultipartFile document,
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

        String documentUrl = fileStorageService.save(document);

        requestService.createRequest(
                currentUser.getUserId(),
                request.getRequestedBloodGroup(),
                request.getHospitalName(),
                documentUrl,
                request.getLatitude(),
                request.getLongitude()
        );

        redirectAttributes.addFlashAttribute("success", "Your request has been submitted for verification!");
        return "redirect:/profile";
    }
}