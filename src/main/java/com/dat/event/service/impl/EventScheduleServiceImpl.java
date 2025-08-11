/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.mappers.EventScheduleMapper;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.dto.RequestEventPlanDto;
import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.service.EventScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

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


    @Override
    public void saveEventSchedule(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String staffNo) {
        // create schedule, (eventId,date,startTime,endTime,createAt,createBy,updateAt,updateBy)
        Arrays.stream(requestEventPlanDto.getEventTimes()).forEach(eventTime -> {
            var date = eventTime.startDateTime().toLocalDate();
            var start = eventTime.startDateTime().toLocalTime();
            var end = eventTime.endDateTime().toLocalTime();
            var dto = EventScheduleDto.builder()
                    .eventDto(eventDto)
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
}
