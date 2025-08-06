/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EventSchedule Class.
 * <p>
 * </p>
 *
 * @author
 */
@Entity
@Data
@Table(name = "tbl_event_schedule")
public class EventScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("timeSlotId")
    @JoinColumn(name = "time_slot_id", nullable = false)
    @JsonIgnore
    private TimeSlotEntity timeSlot;

    private LocalDate date;
    private boolean delFlag;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;


}
