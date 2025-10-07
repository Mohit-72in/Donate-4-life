package com.donate4life.demo.service;

import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.exception.ResourceNotFoundException;
import com.donate4life.demo.repository.RequestRepository;
import com.donate4life.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public Request createRequest(Integer acceptorId, String bloodGroup, String hospitalName, String documentUrl) {
        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow(() -> new ResourceNotFoundException("Acceptor not found with ID: " + acceptorId));

        Request request = new Request(acceptor, bloodGroup, hospitalName);
        request.setDocumentUrl(documentUrl);
        return requestRepository.save(request);
    }

    public Request getRequestById(Integer requestId) {
        return requestRepository.findByIdWithAcceptor(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));
    }

    public List<Request> getPendingRequests() {
        // This finds requests that are APPROVED and still PENDING fulfillment
        return requestRepository.findByVerificationStatusAndStatusWithAcceptor(
                Request.VerificationStatus.APPROVED,
                Request.Status.PENDING
        );
    }

    public List<Request> getUnverifiedRequests() {
        // This now correctly calls the method that fetches the acceptor details
        return requestRepository.findByVerificationStatusWithAcceptor(Request.VerificationStatus.UNVERIFIED);
    }

    public void verifyRequest(Integer requestId, boolean isApproved) {
        Request request = getRequestById(requestId);
        if (isApproved) {
            request.setVerificationStatus(Request.VerificationStatus.APPROVED);
        } else {
            request.setVerificationStatus(Request.VerificationStatus.REJECTED);
        }
        requestRepository.save(request);
    }
}