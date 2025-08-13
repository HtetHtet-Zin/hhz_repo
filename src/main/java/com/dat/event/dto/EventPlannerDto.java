package com.dat.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class EventPlannerDto {

    private Long staffId;
    private Long eventId;
    private String supportedMonth;
    private Boolean supportedMemberFlg;
    private Boolean delFlg;

    private String eventName;
    private String incharge_person;
    private String supported_members;
}
