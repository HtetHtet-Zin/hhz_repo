package com.dat.event.repository;

import com.dat.event.dto.PlannerDto;
import com.dat.event.entity.EventPlannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventPlannerRepository extends JpaRepository<EventPlannerEntity,Long> {

    @Query(value = "SELECT eve.name AS event_name, " +
            "    GROUP_CONCAT(CASE WHEN plan.supported_member_flg = 0  " +
            "                      THEN sta.name " +
            "                 END) AS incharge_person, " +
            "    GROUP_CONCAT(CASE WHEN plan.supported_member_flg = 1  " +
            "                      THEN CONCAT(sta.name, '(', plan.supported_month, ')') " +
            "                 END SEPARATOR ', ') AS supported_members, eve.event_id " +
            "FROM tbl_event_planner plan " +
            "JOIN tbl_staff sta ON sta.staff_id = plan.staff_id " +
            "JOIN tbl_event eve ON eve.event_id = plan.event_id " +
            "WHERE eve.event_id = :eventId " +
            "GROUP BY eve.event_id;", nativeQuery = true
    )
    List<Object[]> getEventWithSchedule(@Param("eventId") long eventId);


    @Query("""
    SELECT new com.dat.event.dto.PlannerDto(
        ep.id,
        ep.event.eventId,
        ep.staff.staffId,
        ep.staff.staffNo,
        ep.staff.name,
        ep.supportedMonth
    )
    FROM EventPlannerEntity ep
    WHERE ep.supportedMemberFlg = false
      AND ep.event.eventId = :eventId
      AND ep.delFlg = false
""")
    PlannerDto getInChargePerson(@Param("eventId") Long eventId);

    @Query("""
    SELECT new com.dat.event.dto.PlannerDto(
        ep.id,
        ep.event.eventId,
         ep.staff.staffId,
        ep.staff.staffNo,
        ep.staff.name,
        ep.supportedMonth
    )
    FROM EventPlannerEntity ep
    WHERE ep.supportedMemberFlg = true
      AND ep.event.eventId = :eventId
      AND ep.delFlg = false
""")
    List<PlannerDto> getSupportedMember(@Param("eventId") Long eventId);

    @Query(value = "SELECT id FROM tbl_event_planner WHERE event_id = :eventId AND supported_member_flg ",nativeQuery = true)
    List<Long>  getEventPlannerIds(@Param("eventId") Long eventId);
}
