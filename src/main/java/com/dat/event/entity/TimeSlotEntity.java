/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * TimeSlotEntity Class.
 * <p>
 * </p>
 *
 * @author
 */
@Entity
@Data
@Table(name = "tbl_time_slot")
public class TimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Long timeSlotId;

    private String timeSlot;
    private  boolean delFlag;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "timeSlot")
//    private List<EventScheduleEntity> eventScheduleEntityList = new ArrayList<>();
}
