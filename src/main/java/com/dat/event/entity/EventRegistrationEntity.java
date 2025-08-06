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

/**
 * EventLeader Class.
 * <p>
 * </p>
 *
 * @author
 */
@Entity
@Data
@Table(name = "tbl_event_registration")
public class EventRegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("staffId")
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnore
    private StaffEntity staff;

    private boolean inChargeFlag;
}
