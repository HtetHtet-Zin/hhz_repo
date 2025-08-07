package com.dat.event.service.impl;

import com.dat.event.dto.EventStaffDto;
import com.dat.event.repository.EventRegistrationRepository;
import com.dat.event.service.EventRegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;

    @Override
    public List<EventStaffDto> fetchEventStaffList() {
        return eventRegistrationRepository.fetchEventStaffList();
    }
}
