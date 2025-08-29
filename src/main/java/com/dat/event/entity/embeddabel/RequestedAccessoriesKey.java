/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity.embeddabel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RequestedAccessoriesKey Class.
 * <p>
 * </p>
 *
 * @author
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestedAccessoriesKey implements Serializable {

    private Long bookingId;
    private Long accessoriesId;
}
