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

import java.time.LocalDate;
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
            SELECT sch.id, sch.start_time, sch.end_time, sch.date, eve.name, COUNT(reg.id), sch.booking_flag
            FROM tbl_event_schedule sch JOIN tbl_event eve
                ON sch.event_id = eve.event_id
            LEFT JOIN tbl_event_registration reg
                  ON reg.schedule_id = sch.id
            WHERE sch.event_id = :eventId
            AND (sch.booking_flag IS NULL OR sch.booking_flag = 1)
            AND (:keyword IS NULL OR date LIKE %:keyword%)
            GROUP BY sch.id, sch.start_time, sch.end_time, sch.date, eve.name, sch.booking_flag
            ORDER BY sch.date ASC, sch.start_time ASC, sch.end_time ASC
            """, nativeQuery = true)
    Page<Object[]> getScheduleByEventId(@Param("eventId") Long eventId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
            SELECT sch.id, sch.start_time, sch.end_time, sch.date, eve.name, COUNT(reg.id), sch.booking_flag
            FROM tbl_event_schedule sch JOIN tbl_event eve
                ON sch.event_id = eve.event_id
            LEFT JOIN tbl_event_registration reg
                  ON reg.schedule_id = sch.id
            WHERE sch.event_id = :eventId
            AND (:keyword IS NULL OR date LIKE %:keyword%)
            GROUP BY sch.id, sch.start_time, sch.end_time, sch.date, eve.name, sch.booking_flag
            ORDER BY sch.date ASC, sch.start_time ASC, sch.end_time ASC
            """, nativeQuery = true)
    Page<Object[]> getScheduleByEventIdForBooking(@Param("eventId") Long eventId, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId", nativeQuery = true)
    List<Long> getAllScheduleIdByEvent(@Param("eventId") Long eventId);

    List<EventScheduleEntity> findByEvent_EventIdAndDelFlagFalse(@Param("eventId") Long eventId);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId",nativeQuery = true)
    List<Long> getEventScheduleIds(@Param("eventId") Long eventId);

    /*@Query("SELECT e.id FROM EventScheduleEntity e WHERE e.bookingFlag = false AND e.event.id = :eventId")
    List<Long> pendingSchedules(@Param("eventId") Long eventId);*/

    List<EventScheduleEntity> findByDateAndBookingFlagTrue(LocalDate date);



}
