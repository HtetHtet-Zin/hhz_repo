package com.dat.event.repository;

import com.dat.event.dto.EventStaffDto;
import com.dat.event.entity.EventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("SELECT new com.dat.event.dto.EventStaffDto(" +
            "e.name, s.staffNo, s.name, s.email, s.mobile) " +
            "FROM EventRegistrationEntity er " +
            "JOIN er.event e " +
            "JOIN er.staff s")
    List<EventStaffDto> fetchEventStaffList();

}
