/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.constant.Constants;
import com.dat.event.common.mappers.EventScheduleMapper;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.dto.RequestEventPlanDto;
import com.dat.event.dto.UpdateEventPlanDto;
import com.dat.event.entity.EventPlannerEntity;
import com.dat.event.entity.EventScheduleEntity;
import com.dat.event.repository.EventRegistrationRepository;
import com.dat.event.repository.EventRepository;
import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.service.EventScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EventScheduleServiceImpl Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventScheduleServiceImpl implements EventScheduleService {

    private final EventScheduleRepository eventScheduleRepository;

    private final EventScheduleMapper eventScheduleMapper;

    private final EventRepository eventRepository;

    private final EventRegistrationRepository eventRegistrationRepository;

    @Override
    public void saveEventSchedule(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String staffNo) {
        // create schedule, (eventId,date,startTime,endTime,createAt,createBy,updateAt,updateBy)

        log.info("Event ID {}",eventDto.getEventId());
        log.info("Request DTO {}",requestEventPlanDto);
        Arrays.stream(requestEventPlanDto.getEventTimes()).forEach(eventTime -> {
            var date = eventTime.startDateTime().toLocalDate();
            var start = eventTime.startDateTime().toLocalTime();
            var end = eventTime.endDateTime().toLocalTime();
            var dto = EventScheduleDto.builder()
                    .eventDto(EventDto.builder().eventId(eventDto.getEventId()).build())
                    .name(requestEventPlanDto.getEventName())
                    .description(requestEventPlanDto.getDescription())
                    .date(date)
                    .startTime(start)
                    .endTime(end)
                    .createdBy(staffNo)
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .updatedBy(staffNo)
                    .delFlg(false)
                    .build();
            eventScheduleRepository.save(eventScheduleMapper.toEntity(dto));
        });
    }

    @Override
    public Page<EventScheduleDto> getScheduleById(Long eventId, String keyword, int page) {
        Page<Object[]> schedules = eventScheduleRepository.getScheduleByEventId(
                eventId,
                keyword,
                PageRequest.of(page, Constants.PAGE_LIMIT)
        );

        return schedules.map(objects -> EventScheduleDto.builder()
                .id(objects[0] != null ? Long.valueOf(objects[0].toString()) : null)
                .startTime(objects[1] != null ? LocalTime.parse(objects[1].toString()) : null)
                .endTime(objects[2] != null ? LocalTime.parse(objects[2].toString()) : null)
                .date(objects[3] != null ? LocalDate.parse(objects[3].toString()) : null)
                .name(objects[4] != null ? objects[4].toString() : null)
                .participantCount(objects[5] != null ? Integer.valueOf(objects[5].toString()) : null)
                .build()
        );
    }

    @Override
    public List<Long> getAllScheduleIdByEvent(Long eventId) {
        return eventScheduleRepository.getAllScheduleIdByEvent(eventId);
    }

    @Override
    public void updateEventSchedule(EventDto eventDto, UpdateEventPlanDto requestEventPlanDto, String staffNo) {
        log.info("Event ID {}",eventDto.getEventId());
        log.info("Request DTO {}",requestEventPlanDto);
        List<Long> saveScheduleList = new ArrayList<>();
        requestEventPlanDto.getEventTimes().forEach(eventTime ->{
            EventScheduleEntity entity;
            if (eventTime.getEventTimeId() != null) {
                // Update mode
                entity = eventScheduleRepository.findById(eventTime.getEventTimeId())
                        .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
            } else {
                // Insert mode
                entity = new EventScheduleEntity();
            }

            entity.setDate( eventTime.getStartDateTime().toLocalDate());
            entity.setEvent(eventRepository.getReferenceById(eventDto.getEventId()));
            entity.setStartTime(eventTime.getStartDateTime().toLocalTime());
            entity.setEndTime(eventTime.getEndDateTime().toLocalTime());
            entity.setDelFlag(false);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setCreatedBy(staffNo);
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(staffNo);
            saveScheduleList.add(eventScheduleRepository.save(entity).getId());

        });
        eventScheduleRepository.getEventScheduleIds(eventDto.getEventId()).stream()
                .filter(scheduleId -> !saveScheduleList.contains(scheduleId))
                .forEach( scheduleId ->{
                var    entity = eventScheduleRepository.findById(scheduleId)
                            .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
                    entity.setDelFlag(true);
                    eventScheduleRepository.save(entity);
                          }
                );

    }

    @Override
    public List<EventScheduleDto> getEventSchedule(Long eventId) {
        return eventScheduleMapper.toDtoList(eventScheduleRepository.findByEvent_EventIdAndDelFlagFalse(eventId));
    }

    @Transactional
    @Override
    public void deleteSchedule(Long eventId) {
        List<Long> scheduleIds = eventScheduleRepository.getEventScheduleIds(eventId);
        eventRegistrationRepository.deleteRegistration(scheduleIds);
        eventScheduleRepository.deleteAllByIdInBatch(scheduleIds);
    }
}
