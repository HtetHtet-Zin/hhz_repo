/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.repository;

import com.dat.event.entity.RequestedAccessoriesEntity;
import com.dat.event.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * RequestedAccessoriesEntity Class.
 * <p>
 * </p>
 *
 * @author
 */
public interface RequestedAccessoriesRepository extends JpaRepository<RequestedAccessoriesEntity, Long> {

    @Query(value = "SELECT ra.accessories.name FROM RequestedAccessoriesEntity ra WHERE ra.booking.id = :bookingId")
    List<String> getAccessories(@Param("bookingId") Long bookingId);
}
