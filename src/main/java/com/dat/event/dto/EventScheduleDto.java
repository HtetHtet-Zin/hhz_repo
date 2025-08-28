/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * EventScheduleDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Data
@Builder
public class EventScheduleDto {

    private Long id;
    private EventDto eventDto;
    private String name;
    private String description;
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updateAt;
    private Boolean delFlg;
    private Boolean bookingFlag;
    private Integer participantCount;

    private String fromTime;
    private String toTime;
}
