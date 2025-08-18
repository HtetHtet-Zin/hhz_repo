/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * UpdateEventPlanDto Class.
 * <p>
 * </p>
 *
 * @author
 */
@Data
@Builder
public class UpdateEventPlanDto {

    private Long eventId;
    private String eventName;
    private String description;
    private Long inChargePersonPlannerId;
    private Long inChargePerson;
    private List<updateSupportedMember> supportedMembers;
    private List<updateEventTime> eventTimes;
    private List<Long> deleteScheduleList;
}
