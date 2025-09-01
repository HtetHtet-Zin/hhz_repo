/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.repository;

import com.dat.event.dto.BookingDto;
import com.dat.event.entity.BookingEntity;
import com.dat.event.entity.EventPlannerEntity;
import com.dat.event.entity.StaffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * BookingRepository Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

    @Query(value = """
               SELECT DISTINCT
                   b.booking_id AS bookingId,
                   b.event_name AS eventName,
                   b.booked_date AS bookedDate,
                   b.status AS status,
                   s.name AS name,
                   s.team AS team,             
                   s.department AS department,
                    es.date AS date,
                   es.start_time AS startTime,
                   es.end_time AS endTime,
                   b.schedule_id AS scheduleId,
                   b.attendees
               FROM tbl_booking b
               INNER JOIN tbl_staff s ON s.staff_no = b.booked_by
               INNER JOIN tbl_event_schedule es ON es.id = b.schedule_id
               WHERE b.del_flag = false
               AND es.date >= :tdyDate
               AND  (:keyword IS NULL 
                   OR LOWER(b.event_name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                   OR LOWER(b.status) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.team) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%')))
               ORDER BY  b.booked_date ASC
                """,
            nativeQuery = true
    )
    Page<Object[]> getAllBooking(@Param("tdyDate")LocalDate tdyDate, @Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM tbl_booking WHERE schedule_id IN (:scheduleIds)", nativeQuery = true)
    void deleteBooking(@Param("scheduleIds") List<Long> scheduleIds);

    boolean existsByScheduleIdIn(List<Long> scheduleIds);

    @Query(value = """
            SELECT sta.name, sta.staff_no, sta.team, sta.department, book.attendees,
            book.purpose, GROUP_CONCAT(acc.acce_Id) AS accessories FROM event.tbl_booking book
            JOIN tbl_staff sta
            	ON sta.staff_no = book.booked_by
            JOIN tbl_event_schedule sch
            	ON sch.id = book.schedule_id
            LEFT JOIN tbl_requested_accessories req
                ON req.booking_id = book.booking_id
            LEFT JOIN tbl_accessories acc
                ON acc.acce_id = req.accessories_id
            WHERE book.schedule_id = :scheduleId
            GROUP BY
                sta.name,
                sta.staff_no,
                sta.team,
                sta.department,
                book.attendees,
                book.purpose;
            """,
            nativeQuery = true
    )
    Object[] getBookingSchedule(@Param("scheduleId") Long scheduleId);
}
