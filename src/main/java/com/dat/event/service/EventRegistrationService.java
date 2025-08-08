package com.dat.event.service;

import com.dat.event.dto.EventStaffDto;
import org.springframework.data.domain.Page;


public interface EventRegistrationService {

    Page<EventStaffDto> fetchEventStaffList(String keyword, int page);

    int registerCount();
}
