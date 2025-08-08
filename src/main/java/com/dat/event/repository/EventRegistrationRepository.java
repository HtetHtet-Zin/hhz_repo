package com.dat.event.repository;

import com.dat.event.dto.EventStaffDto;
import com.dat.event.entity.EventRegistrationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("SELECT new com.dat.event.dto.EventStaffDto( " +
            "e.name, s.staffNo, s.name, sch.startTime, sch.endTime) " +
            "FROM EventRegistrationEntity er " +
            "JOIN er.schedule sch " +
            "JOIN sch.event e " +
            "JOIN er.staff s " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.staffNo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY function('RAND')")
    Page<EventStaffDto> fetchEventStaffList(@Param("keyword") String keyword, Pageable pageable);



}
