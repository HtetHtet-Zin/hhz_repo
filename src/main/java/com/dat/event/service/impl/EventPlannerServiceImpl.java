package com.dat.event.service.impl;

import com.dat.event.common.mappers.EventPlannerMapper;
import com.dat.event.common.mappers.StaffMapper;
import com.dat.event.dto.*;
import com.dat.event.entity.EventPlannerEntity;
import com.dat.event.repository.EventPlannerRepository;
import com.dat.event.repository.EventRepository;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.EventPlannerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventPlannerServiceImpl implements EventPlannerService {

    private final EventPlannerRepository eventPlannerRepository;
    private final EventPlannerMapper eventPlannerMapper;
    private final StaffRepository staffRepository;
    private final EventRepository eventRepository;

    @Override
    public void saveEventPlanner(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String loginStaffNo) {
        // step-3 create eventPlanner, (staffId,eventId,supportedMonth,supportedMemberFlg,delFlg)

        var eventId = eventDto.getEventId();
        eventPlannerRepository.save(eventPlannerMapper.toEntity(EventPlannerDto.builder()
                .eventId(eventId)
                .staffId(requestEventPlanDto.getInChargePerson())
                .supportedMemberFlg(false)
                .build()));

        Arrays.stream(requestEventPlanDto.getSupportedMembers()).forEach(supportedMember -> {
            var dto = EventPlannerDto.builder()
                    .eventId(eventId)
                    .staffId(supportedMember.memberId())
                    .supportedMonth(supportedMember.month())
                    .supportedMemberFlg(true)
                    .delFlg(false)
                    .build();
            eventPlannerRepository.save(eventPlannerMapper.toEntity(dto));
        });

    }

    @Override
    public EventPlannerDto getEventWithSchedule(long eventId) {
        List<Object[]> results = eventPlannerRepository.getEventWithSchedule(eventId);
        if (results.isEmpty()) {
            // No data found for this eventId
            return null;  // or throw an exception or return an empty DTO as appropriate
        }
        Object[] result = results.get(0);
        return EventPlannerDto.builder()
                .eventName(result[0] != null ? result[0].toString() : null)
                .incharge_person(result[1] != null ? result[1].toString() : null)
                .supported_members(result[2] != null ? result[2].toString() : null)
                .eventId(result[3] != null ? Long.valueOf(result[3].toString()) : null)
                .build();
    }

    @Override
    public void updateEventPlanner(EventDto eventDto, UpdateEventPlanDto requestEventPlanDto, String loginStaffNo) {

        EventPlannerEntity entity;

        if (requestEventPlanDto.getInChargePersonPlannerId() != null) {
            // Update mode
            entity = eventPlannerRepository.findById(requestEventPlanDto.getInChargePersonPlannerId())
                    .orElseThrow(() -> new EntityNotFoundException("Planner not found"));
        } else {
            // Insert mode
            entity = new EventPlannerEntity();
        }

        entity.setStaff(staffRepository.getReferenceById(requestEventPlanDto.getInChargePerson()));
        entity.setEvent(eventRepository.getReferenceById(eventDto.getEventId()));
        entity.setSupportedMonth(null);
        entity.setSupportedMemberFlg(false);
        entity.setDelFlg(false);

        eventPlannerRepository.save(entity);

        if(requestEventPlanDto.getSupportedMembers() != null ){
            updateSupportedMember(requestEventPlanDto.getSupportedMembers(),eventDto);
        }

    }

    private void updateSupportedMember(List<updateSupportedMember> updateSupportedMembersList,EventDto eventDto){

        List<Long> savedPlannerIdList = new ArrayList<>();
        updateSupportedMembersList.forEach(supportedMember -> {
            EventPlannerEntity entity;
            savedPlannerIdList.add(supportedMember.getPlannerId());
            if (supportedMember.getPlannerId() != null) {
                // Update mode
                entity = eventPlannerRepository.findById(supportedMember.getPlannerId())
                        .orElseThrow(() -> new EntityNotFoundException("Planner not found"));
            } else {
                // Insert mode
                entity = new EventPlannerEntity();
            }

            entity.setStaff(staffRepository.getReferenceById(supportedMember.getMemberId()));
            entity.setEvent(eventRepository.getReferenceById(eventDto.getEventId()));
            entity.setSupportedMonth(supportedMember.getMonth());
            entity.setSupportedMemberFlg(true);
            entity.setDelFlg(false);

            eventPlannerRepository.save(entity);
        });

     eventPlannerRepository.getEventPlannerIds(eventDto.getEventId()).stream()
              .filter(plannerId -> !savedPlannerIdList.contains(plannerId))
             .forEach(eventPlannerRepository::deleteById);


    }


    @Override
    public PlannerDto getInChargePerson(long eventId) {
        return eventPlannerRepository.getInChargePerson(eventId);
    }

    @Override
    public List<PlannerDto> getSupportedMember(long eventId) {
        return eventPlannerRepository.getSupportedMember(eventId);
    }
}
