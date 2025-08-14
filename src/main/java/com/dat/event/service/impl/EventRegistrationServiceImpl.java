package com.dat.event.service.impl;

import com.dat.event.common.CommonUtility;
import com.dat.event.common.constant.Constants;
import com.dat.event.common.excel.ExcelUtility;
import com.dat.event.dto.EventStaffDto;
import com.dat.event.entity.EventRegistrationEntity;
import com.dat.event.entity.EventScheduleEntity;
import com.dat.event.entity.StaffEntity;
import com.dat.event.repository.EventRegistrationRepository;
import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.EventRegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


@Slf4j
@Service
@AllArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final StaffRepository staffRepository;
    private final EventScheduleRepository eventScheduleRepository;

    @Override
    public Page<EventStaffDto> fetchEventStaffList(String keyword, final int page) {
        return eventRegistrationRepository.fetchEventStaffList(keyword, PageRequest.of(page, Constants.PAGE_LIMIT));
    }

    @Override
    public int registerCount() {
        return (int) eventRegistrationRepository.count();
    }

    @Override
    public List<Long> getRegisteredSchedule(Long eventId, Long staffId) {
        return eventRegistrationRepository.getRegisteredSchedule(eventId, staffId);
    }

    @Override
    @Transactional
    public int registerEvent(Long staffId, List<Long> registeredScheduleIds, Long eventId) {
        int count = 0;
        eventRegistrationRepository.deleteSchedule(eventId,staffId);
        StaffEntity staff = staffRepository.findById(staffId).orElseThrow(() -> new IllegalArgumentException("Invalid Staff ID"));
        if (registeredScheduleIds == null) return 1;
        for (Long scheduleId : registeredScheduleIds) {
            EventScheduleEntity schedule = eventScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID"));
            EventRegistrationEntity registration = new EventRegistrationEntity();
            registration.setStaff(staff);
            registration.setSchedule(schedule);
            eventRegistrationRepository.save(registration);
            count++;
        }
        return count;
    }

    @Override
    public List<String> checkDuplicateSchedule(Long staffId, List<Long> registeredScheduleIds) {
        return eventRegistrationRepository.checkDuplicateSchedules(staffId, registeredScheduleIds).stream()
                .map(index -> {
                    LocalDate date = index[0] != null ? LocalDate.parse(index[0].toString()) : null;
                    LocalTime start = index[1] != null ? LocalTime.parse(index[1].toString()) : null;
                    LocalTime end = index[2] != null ? LocalTime.parse(index[2].toString()) : null;
                    return date + " (" + start + " - " + end + ")";
                }).toList();
    }

    @Override
    public byte[] exportExcel(final String keyword) {
        log.info("Event statt excel file exporting..");
        final AtomicInteger index = new AtomicInteger();
        return ExcelUtility.writeExcelFile(Stream.concat(
                //Set header
                Stream.of(List.of("No.", "Event	Date", "Schedule Time", "Staff-ID", "Staff Name")),
                //Set data
                fetchEventStaffList(keyword.isBlank() ? null : keyword).stream().map(report -> {
                    final List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(index.incrementAndGet());
                    rowDataList.add(CommonUtility.ifNoDataReturnDash(String.valueOf(report.getDate())));
                    rowDataList.add(CommonUtility.ifNoDataReturnDash(report.getStartTime() + " - " + report.getEndTime()));
                    rowDataList.add(report.getStaffNo());
                    rowDataList.add(report.getStaffName());
                    return rowDataList;
                })
        ).toList(), "Staff in the Event");
    }

    @Override
    public List<EventStaffDto> fetchEventStaffList(final String keyword) {
        return eventRegistrationRepository.fetchEventStaffList(keyword);
    }
}
