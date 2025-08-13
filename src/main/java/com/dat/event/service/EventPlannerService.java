package com.dat.event.service;

import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventPlannerDto;
import com.dat.event.dto.RequestEventPlanDto;

import java.util.List;

public interface EventPlannerService {

    void saveEventPlanner(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String loginStaffNo);

    EventPlannerDto getEventWithSchedule(long eventId);
}
