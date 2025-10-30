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

    public Request createRequest(Integer acceptorId, String bloodGroup, String hospitalName, String documentUrl,
                                 Double latitude, Double longitude) { // Includes Lat/Lng
        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow(() -> new ResourceNotFoundException("Acceptor not found with ID: " + acceptorId));

        Request request = new Request(acceptor, bloodGroup, hospitalName);
        request.setDocumentUrl(documentUrl);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        return requestRepository.save(request);
    }

    public Request getRequestById(Integer requestId) {
        return requestRepository.findByIdWithAcceptor(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));
    }

    public List<Request> getPendingRequests() {
        return requestRepository.findByVerificationStatusAndStatusWithAcceptor(
                Request.VerificationStatus.APPROVED,
                Request.Status.PENDING
        );
    }

    public List<Request> getUnverifiedRequests() {
        return requestRepository.findByVerificationStatusWithAcceptor(Request.VerificationStatus.UNVERIFIED);
    }

    public void verifyRequest(Integer requestId, boolean isApproved) {
        Request request = getRequestById(requestId); // Use getRequestById to ensure donor is fetched
        if (isApproved) {
            request.setVerificationStatus(Request.VerificationStatus.APPROVED);
        } else {
            request.setVerificationStatus(Request.VerificationStatus.REJECTED);
        }
        requestRepository.save(request);
    }

    public List<Request> getRequestsByAcceptor(Integer acceptorId) {
        return requestRepository.findByAcceptor_UserId(acceptorId);
    }

    // ⭐ --- ADD THIS NEW METHOD --- ⭐
    /**
     * Marks a request as FULFILLED.
     * This will remove it from the admin's "Active Requests" list.
     * @param requestId The ID of the request to fulfill.
     */
    public void fulfillRequest(Integer requestId) {
        // FindById is fine here, but getRequestById is safer if it fetches relationships
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        // Set the status to FULFILLED
        request.setStatus(Request.Status.FULFILLED);

        // Save the change to the database
        requestRepository.save(request);
    }
}