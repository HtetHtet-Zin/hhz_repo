/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.repository;

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
            SELECT sch.id, sch.start_time, sch.end_time, sch.date, eve.name, COUNT(reg.id), sch.booking_flag, sch.reject_flag
            FROM tbl_event_schedule sch JOIN tbl_event eve
                ON sch.event_id = eve.event_id
            LEFT JOIN tbl_event_registration reg
                  ON reg.schedule_id = sch.id
            WHERE sch.event_id = :eventId
             AND sch.date >= :tdyDate
             AND (
                     :booking = false
                     OR (sch.booking_flag IS NULL OR sch.booking_flag = 1)
                   )
            AND (
                     :booking = true
                     OR (sch.booking_flag IS NULL OR sch.booking_flag in (0,1))
                   )
            AND (:keyword IS NULL OR date LIKE %:keyword%)
            GROUP BY sch.id, sch.start_time, sch.end_time, sch.date, eve.name, sch.booking_flag, sch.reject_flag
            ORDER BY sch.date ASC, sch.start_time ASC, sch.end_time ASC
            """, nativeQuery = true)
    Page<Object[]> getScheduleByEventId(@Param("eventId") Long eventId,@Param("tdyDate") LocalDate tdyDate,@Param("booking") boolean booking, @Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId AND date >= :todayDate", nativeQuery = true)
    List<Long> getAllScheduleIdByEvent(@Param("eventId") Long eventId, @Param("todayDate") LocalDate todayDate);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId AND date >= :todayDate AND (booking_flag IS NULL OR booking_flag = 1)", nativeQuery = true)
    List<Long> getAllScheduleIdByEventForParticipant(@Param("eventId") Long eventId, @Param("todayDate") LocalDate todayDate);

    List<EventScheduleEntity> findByEvent_EventId(@Param("eventId") Long eventId);

    @Query(value = "SELECT id FROM tbl_event_schedule WHERE event_id = :eventId",nativeQuery = true)
    List<Long> getEventScheduleIds(@Param("eventId") Long eventId);

    /*@Query("SELECT e.id FROM EventScheduleEntity e WHERE e.bookingFlag = false AND e.event.id = :eventId")
    List<Long> pendingSchedules(@Param("eventId") Long eventId);*/

    List<EventScheduleEntity> findByDateAndBookingFlagTrue(LocalDate date);

    @Query("SELECT event.name FROM EventScheduleEntity WHERE id = :id")
    String findEventNameById(Long id);

}
