package com.dat.event.service;

import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventPlannerDto;
import com.dat.event.dto.RequestEventPlanDto;

public interface EventPlannerService {

    EventPlannerDto saveEventPlanner(EventPlannerDto dto);

    void saveEventPlanner(EventDto eventId, RequestEventPlanDto requestEventPlanDto, String loginStaffNo);
}
