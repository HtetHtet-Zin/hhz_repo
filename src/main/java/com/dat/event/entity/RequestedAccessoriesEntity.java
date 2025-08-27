/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import com.dat.event.entity.embeddabel.RequestedAccessoriesKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

/**
 * RequestedAccessories Class.
 * <p>
 * </p>
 *
 * @author
 */
@Entity
@Data
@Table(name = "tbl_requested_accessories")
public class RequestedAccessoriesEntity {

    @EmbeddedId
    private RequestedAccessoriesKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookingId")
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("AccessoriesId")
    @JoinColumn(name = "acce_id", nullable = false)
    @JsonIgnore
    private AccessoriesEntity accessories;
}
