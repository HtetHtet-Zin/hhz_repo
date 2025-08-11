package com.dat.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventPlannerDto {

    private Long staffId;
    private Long eventId;
    private String supportedMonth;
    private Boolean supportedMemberFlg;
    private Boolean delFlg;

}
