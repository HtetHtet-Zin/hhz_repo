package com.dat.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime endTime;
    private LocalDate date;

    public EventStaffDto(String eventName, String staffNo, String staffName, LocalTime startTime, LocalTime endTime, LocalDate date) {
        this.eventName = eventName;
        this.staffNo = staffNo;
        this.staffName = staffName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }
}
