/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * TimeSlotDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Data
public class TimeSlotDto {
    private LocalDate from;
    private LocalDate to;
}
