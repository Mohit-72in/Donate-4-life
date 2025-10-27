package com.donate4life.demo.repository;

import com.donate4life.demo.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    // ⭐ THIS WAS THE MISSING METHOD
    // This is needed by the profile page to get an Acceptor's history
    List<Request> findByAcceptor_UserId(Integer acceptorId);

    // --- Methods for Admin Dashboard ---

    // Fetches APPROVED requests and their acceptors for the "Active Requests" list
    @Query("SELECT r FROM Request r JOIN FETCH r.acceptor WHERE r.verificationStatus = :verStatus AND r.status = :fulfilStatus")
    List<Request> findByVerificationStatusAndStatusWithAcceptor(Request.VerificationStatus verStatus, Request.Status fulfilStatus);

    // Fetches a single request and its acceptor (for the details page)
    @Query("SELECT r FROM Request r JOIN FETCH r.acceptor WHERE r.requestId = :requestId")
    Optional<Request> findByIdWithAcceptor(Integer requestId);

    // Fetches UNVERIFIED requests and their acceptors for the "Verification" list
    @Query("SELECT r FROM Request r JOIN FETCH r.acceptor WHERE r.verificationStatus = :status ORDER BY r.requestDate ASC")
    List<Request> findByVerificationStatusWithAcceptor(Request.VerificationStatus status);

    // Finds requests by both verification and fulfillment status (needed for the service layer)
    List<Request> findByVerificationStatusAndStatus(Request.VerificationStatus verificationStatus, Request.Status status);
}