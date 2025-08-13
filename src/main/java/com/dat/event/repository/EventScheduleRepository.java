/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.repository;

import com.dat.event.dto.EventScheduleDto;
import com.dat.event.entity.EventScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * EventScheduleRepository Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventScheduleRepository extends JpaRepository<EventScheduleEntity, Long> {

    @Query(value = """
            SELECT sch.id, sch.start_time, sch.end_time, sch.date, eve.name
            FROM tbl_event_schedule sch JOIN tbl_event eve
            ON sch.event_id = eve.event_id
            WHERE sch.event_id = :eventId
            AND (:keyword IS NULL OR date LIKE %:keyword%)
            """,
            countQuery = """
        SELECT COUNT(*)
        FROM tbl_event_schedule
        WHERE event_id = :eventId
        AND (:keyword IS NULL OR date LIKE %:keyword%)
        """, nativeQuery = true)
    Page<Object[]> getScheduleByEventId(@Param("eventId") Long eventId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId", nativeQuery = true)
    List<Long> getAllScheduleIdByEvent(@Param("eventId") Long eventId);


}
