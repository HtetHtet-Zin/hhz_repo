/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.EventDto;
import com.dat.event.dto.RequestEventPlanDto;

/**
 * EventScheduleService Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventScheduleService {
    void saveEventSchedule(EventDto eventDto, RequestEventPlanDto requestEventPlanDto, String staffNo);
}
