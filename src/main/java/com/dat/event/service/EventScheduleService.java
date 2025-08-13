/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.dto.RequestEventPlanDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * EventScheduleService Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventScheduleService {
    void saveEventSchedule(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String staffNo);

    Page<EventScheduleDto> getScheduleById(Long eventId, String keyword, int page);

    List<Long> getAllScheduleIdByEvent(Long eventId);
}
