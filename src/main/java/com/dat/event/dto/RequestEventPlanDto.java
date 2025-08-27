package com.dat.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestEventPlanDto {

    private String eventName;
    private String eventLocation;
    private String description;
    private Long inChargePerson;
    private SupportedMember[] supportedMembers;
    private EventTime[] eventTimes;

}


