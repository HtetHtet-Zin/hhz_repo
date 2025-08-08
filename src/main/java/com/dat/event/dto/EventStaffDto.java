package com.dat.event.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class EventStaffDto {

    private String eventName;
    private String staffNo;
    private String staffName;
    private LocalTime startTime;
    private LocalTime endTime;

    public EventStaffDto(String eventName, String staffNo, String staffName, LocalTime startTime, LocalTime endTime) {
        this.eventName = eventName;
        this.staffNo = staffNo;
        this.staffName = staffName;
        this.startTime = startTime;
        this.endTime = endTime;

    }
}
