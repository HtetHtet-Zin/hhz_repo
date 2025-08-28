/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingEntity Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
@Entity
@Data
@Table(name = "tbl_booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private EventScheduleEntity schedule;
    private String eventName;
    private String status;
    private String reason;
    private String confirmedBy;
    private LocalDate confirmedDate;
    private LocalDate bookedDate;
    private String bookedBy;
    private int attendees;
    private boolean delFlag;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestedAccessoriesEntity> requestedAccessoriesEntityList = new ArrayList<>();

}
