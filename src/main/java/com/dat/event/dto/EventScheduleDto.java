/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Data;

import java.util.List;

/**
 * EventScheduleDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Data
public class EventScheduleDto {
    private String name;
    private String description;
    private List<TimeSlotDto> timeSlotDtoList;
}
