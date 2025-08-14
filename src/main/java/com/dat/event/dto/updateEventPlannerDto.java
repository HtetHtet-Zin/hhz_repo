/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Builder;
import lombok.Data;

/**
 * updateEventPlannerDto Class.
 * <p>
 * </p>
 *
 * @author
 */
@Data
@Builder
public class updateEventPlannerDto {
    private Long id;
    private Long staffId;
    private EventDto eventId;
    private String supportedMonth;
    private Boolean supportedMemberFlg;
    private Boolean delFlg;
}
