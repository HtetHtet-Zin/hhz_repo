package com.dat.event.dto;


import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
public class EventStaffDto {

    private String eventName;
    private String staffNo;
    private String staffName;
    private String email;
    private String mobile;

    public EventStaffDto(String eventName, String staffNo, String staffName, String email, String mobile) {
        this.eventName = eventName;
        this.staffNo = staffNo;
        this.staffName = staffName;
        this.email = email;
        this.mobile = mobile;
    }
}
