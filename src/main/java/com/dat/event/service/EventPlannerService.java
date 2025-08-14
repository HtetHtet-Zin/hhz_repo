package com.dat.event.service;

import com.dat.event.dto.*;

import java.util.List;

import java.util.List;

public interface EventPlannerService {

    void saveEventPlanner(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String loginStaffNo);

    EventPlannerDto getEventWithSchedule(long eventId);

    void updateEventPlanner(EventDto eventDto, UpdateEventPlanDto requestEventPlanDto, String loginStaffNo);


    PlannerDto getInChargePerson(long eventId);

    List<PlannerDto> getSupportedMember(long eventId);

    void deletePlanner(long eventId);
}
