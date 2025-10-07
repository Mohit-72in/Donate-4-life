package com.donate4life.demo.dto;

import com.donate4life.demo.entity.Request; // Corrected import from 'Requests' to 'Request'
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    // This now correctly refers to the Status enum inside the Request class
    private Request.Status newStatus;
}