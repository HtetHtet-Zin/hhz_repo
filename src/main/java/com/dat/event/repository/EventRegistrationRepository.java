package com.dat.event.repository;

import com.dat.event.dto.EventStaffDto;
import com.dat.event.entity.EventRegistrationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("SELECT new com.dat.event.dto.EventStaffDto( " +
            "e.name, s.staffNo, s.name, sch.startTime, sch.endTime, sch.date) " +
            "FROM EventRegistrationEntity er " +
            "JOIN er.schedule sch " +
            "JOIN sch.event e " +
            "JOIN er.staff s " +
            "WHERE (:eventName IS NULL " +
            "   OR e.name = :eventName)" +
            "AND (:keyword IS NULL " +
            "   OR LOWER(s.staffNo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY e.createdAt ASC, sch.id ASC")
    Page<EventStaffDto> fetchEventStaffList(@Param("eventName") String name, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.dat.event.dto.EventStaffDto( " +
            "e.name, s.staffNo, s.name, sch.startTime, sch.endTime, sch.date) " +
            "FROM EventRegistrationEntity er " +
            "JOIN er.schedule sch " +
            "JOIN sch.event e " +
            "JOIN er.staff s " +
            "WHERE (:eventName IS NULL " +
            "   OR e.name = :eventName)" +
            "AND (:keyword IS NULL " +
            "   OR LOWER(s.staffNo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY e.createdAt ASC, sch.id ASC")
    List<EventStaffDto> fetchEventStaffList(@Param("keyword") String keyword, @Param("eventName") String eventName);

    @Query(value = "SELECT reg.schedule_id from tbl_event_registration reg JOIN tbl_event_schedule sch ON reg.schedule_id = sch.id\n" +
            "WHERE sch.event_id = :eventId AND reg.staff_id = :staffId", nativeQuery = true)
    List<Long> getRegisteredSchedule(@Param("eventId") Long eventId, @Param("staffId") Long staffId);

    @Modifying
    @Query(value = "DELETE reg FROM tbl_event_registration reg JOIN tbl_event_schedule sch ON reg.schedule_id = sch.id " +
            "WHERE sch.event_id = :eventId AND reg.staff_id = :staffId", nativeQuery = true)
    void deleteSchedule(@Param("eventId") Long eventId, @Param("staffId") Long staffId);

    @Query(value = "SELECT sch1.id, sch1.date, sch1.start_time, sch1.end_time " +
            "FROM tbl_event_schedule sch1 JOIN tbl_event_schedule sch2 " +
            "ON sch1.date = sch2.date " +
            "AND sch1.start_time < sch2.end_time " +
            "AND sch1.end_time > sch2.start_time " +
            "AND sch1.id <> sch2.id " +
            "WHERE sch1.id IN (:registeredSchedule) " +
            "AND sch2.id IN (:registeredSchedule) " +

            "UNION " +

            "SELECT existing_sch.id, existing_sch.date, existing_sch.start_time, existing_sch.end_time " +
            "FROM tbl_event_schedule new_sch JOIN tbl_event_schedule existing_sch " +
            "ON new_sch.date = existing_sch.date " +
            "AND new_sch.start_time < existing_sch.end_time " +
            "AND new_sch.end_time > existing_sch.start_time " +
            "AND existing_sch.id <> new_sch.id " +
            "JOIN tbl_event_registration reg " +
            "ON reg.schedule_id = existing_sch.id " +
            "WHERE reg.staff_id = :staffId " +
            "AND new_sch.id IN (:registeredSchedule) ORDER BY id ASC",
            nativeQuery = true)
    List<Object[]> checkDuplicateSchedules(@Param("staffId") Long staffId, @Param("registeredSchedule") List<Long> registeredSchedule);

    @Modifying
    @Query(value = "DELETE FROM tbl_event_registration WHERE schedule_id IN (:scheduleIds)", nativeQuery = true)
    void deleteRegistration(@Param("scheduleIds") List<Long> scheduleIds);

    @Query("SELECT CASE WHEN COUNT(DISTINCT staff) < 10 THEN true ELSE false END FROM EventRegistrationEntity WHERE schedule.event.eventId = :eventId")
    boolean availableToRegister(@Param("eventId") Long eventId);

}
