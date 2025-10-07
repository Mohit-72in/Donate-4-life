package com.donate4life.demo.dto;

import com.donate4life.demo.entity.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Integer requestId;
    private Integer acceptorId;
    private String requestedBloodGroup;
    private LocalDateTime requestDate;
    private String hospitalName;
    private Request.Status status;
}