package com.dat.event.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class EventStaffDto {

    private String eventName;
    private String staffNo;
    private String staffName;
    private String email;
    private String mobile;
    private LocalDate dob;

    public EventStaffDto(String eventName, String staffNo, String staffName, String email, String mobile, LocalDate dob) {
        this.eventName = eventName;
        this.staffNo = staffNo;
        this.staffName = staffName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
    }
}
