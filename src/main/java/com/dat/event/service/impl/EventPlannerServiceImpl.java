package com.dat.event.service.impl;

import com.dat.event.common.mappers.EventPlannerMapper;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.RequestEventPlanDto;
import com.dat.event.repository.EventPlannerRepository;
import com.dat.event.service.EventPlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventPlannerServiceImpl implements EventPlannerService {

    private final EventPlannerRepository eventPlannerRepository;
    private final EventPlannerMapper eventPlannerMapper;

    @Override
    public void saveEventPlanner(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String loginStaffNo) {
        // step-3 create eventPlanner, (staffId,eventId,supportedMonth,supportedMemberFlg,delFlg)

        var eventId = eventDto.getEventId();
        var inChargePersonId = requestEventPlanDto.getInChargePerson();
        var supportedMembers = requestEventPlanDto.getSupportedMembers();

//        Arrays.stream(supportedMembers).forEach();

    }
}
