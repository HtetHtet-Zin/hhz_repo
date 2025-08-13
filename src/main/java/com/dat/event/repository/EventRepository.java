/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.repository;

import com.dat.event.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * EventRepository Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    EventEntity findByName(String eventName);

}
