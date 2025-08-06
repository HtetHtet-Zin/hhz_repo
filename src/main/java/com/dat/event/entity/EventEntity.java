/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * EventEntity Class.
 * <p>
 * </p>
 *
 * @author
 */
@Entity
@Data
@Table(name = "tbl_event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventName;
    private String description;
    private boolean delFlag;
    private LocalDateTime createdAt;
    private String createdBy;
}
