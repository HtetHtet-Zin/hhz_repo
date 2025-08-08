package com.dat.event.service.impl;

import com.dat.event.common.constant.Constants;
import com.dat.event.common.mappers.StaffMapper;
import com.dat.event.dto.StaffDto;
import com.dat.event.entity.StaffEntity;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.StaffService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Override
    public boolean existsByStaffNo(String staffNo, HttpSession session) {
        Optional<StaffEntity> optionalStaff = staffRepository.findByStaffNo(staffNo);

        if (optionalStaff.isPresent()) {
            StaffEntity staff = optionalStaff.get();
            session.setAttribute("id", staff.getStaffId());
            session.setAttribute("staffNo", staff.getStaffNo());
            session.setAttribute("name", staff.getName());
            session.setAttribute("adminFlag", staff.getAdminFlag());
            return true;
        }

        return false;
    }

    @Override
    public Page<StaffDto> findAll(final int page) {
        return staffRepository.findAll(PageRequest.of(page, Constants.PAGE_LIMIT, Sort.by("createdAt").descending())).map(staffMapper::toDTO);
    }

    @Override
    public Page<StaffDto> findAll(final String keyword, final int page) {
        return staffRepository.findByNameContainingIgnoreCaseOrStaffNoContainingIgnoreCase(keyword, keyword, PageRequest.of(page, Constants.PAGE_LIMIT, Sort.by("createdAt").descending())).map(staffMapper::toDTO);
    }

    @Override
    public List<StaffDto> findAll() {
        return staffMapper.toDtoList(staffRepository.findAll());
    }

    @Override
    public void updateAdminFlag(final String staffNo, final boolean adminFlag) {
        final StaffEntity staffEntity = staffRepository.findByStaffNo(staffNo).orElseThrow();
        staffEntity.setAdminFlag(adminFlag);
        staffRepository.save(staffEntity);
    }

    @Override
    public List<StaffDto> birthdayStaffList() {

        LocalDate today = LocalDate.now();
       int month = today.getMonthValue();
        return staffMapper.toDtoList(staffRepository.birthdayStaff(month));
    }
}
