package com.dat.event.dto;

import lombok.Data;

@Data
public class EventPlannerDto {

    private Long staffId;
    private Long eventId;
    private Boolean supportedMemberFlg;
    private Boolean delFlg;

}
