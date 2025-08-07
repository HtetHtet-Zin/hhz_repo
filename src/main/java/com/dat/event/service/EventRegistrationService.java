package com.dat.event.service;

import com.dat.event.dto.EventStaffDto;

import java.util.List;

public interface EventRegistrationService {

    List<EventStaffDto> fetchEventStaffList();
}
