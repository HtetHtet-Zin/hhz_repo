/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PlannerDto Class.
 * <p>
 * </p>
 *
 * @author
 */
@Data
@NoArgsConstructor
public class PlannerDto {

    private Long plannerId;
    private Long eventId;
    private Long staffId;
    private String staffNo;
    private String name;
    private String supportedMonth;

    public PlannerDto( Long plannerId, Long eventId,Long staffId,String staffNo, String name, String supportedMonth) {
        this.plannerId = plannerId;
        this.staffNo = staffNo;
        this.staffId= staffId;
        this.name = name;
        this.eventId = eventId;
        this.supportedMonth = supportedMonth;
    }
}
