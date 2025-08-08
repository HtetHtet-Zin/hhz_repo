package com.dat.event.service.impl;

import com.dat.event.common.constant.Constants;
import com.dat.event.dto.EventStaffDto;
import com.dat.event.repository.EventRegistrationRepository;
import com.dat.event.service.EventRegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;

    @Override
    public Page<EventStaffDto> fetchEventStaffList(String keyword, final int page) {
        return eventRegistrationRepository.fetchEventStaffList(keyword, PageRequest.of(page, Constants.PAGE_LIMIT));
    }

    @Override
    public int registerCount() {
        return (int) eventRegistrationRepository.count();
    }
}
