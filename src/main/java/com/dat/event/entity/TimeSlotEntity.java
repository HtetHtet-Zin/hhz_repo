/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private Long timeSlotId;

    private String timeSlot;
    private  boolean delFlag;
}
