package com.dat.event.service;

import com.dat.event.dto.EventStaffDto;
import org.springframework.data.domain.Page;

import java.util.List;


public interface EventRegistrationService {

    Page<EventStaffDto> fetchEventStaffList(String keyword, int page);

    int registerCount();

    List<Long> getRegisteredSchedule(Long eventId, Long staffId);

    int registerEvent(Long staffId, List<Long> registeredScheduleIds, Long eventId);

    List<String> checkDuplicateSchedule(Long staffId, List<Long> registeredScheduleIds);
}
