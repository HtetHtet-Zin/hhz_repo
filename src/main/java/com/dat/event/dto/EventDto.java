/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * EventDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Data
public class EventDto {
    private String name;
    private String description;
    private boolean delFlag;
    private LocalDateTime createdAt;
    private String createdBy;
}
