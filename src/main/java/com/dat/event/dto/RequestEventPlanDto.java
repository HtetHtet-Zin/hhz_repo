package com.dat.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestEventPlanDto {

    private String eventName;
    private String description;
    private String inChargePerson;
    private SupportedMember[] supportedMembers;
    private EventTime[] eventTimes;

    public record EventTime(
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime startDateTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime endDateTime) {
    }

    public record SupportedMember(String name, String month) {
    }
}


