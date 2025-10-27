/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EventDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private Long eventId;
    private String name;
    private String eventPhoto;
    private String description;
    private boolean delFlag;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String location;
    private String updatedBy;
}
