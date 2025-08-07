package com.dat.event.service.impl;

import com.dat.event.common.constant.Constants;
import com.dat.event.common.mappers.StaffMapper;
import com.dat.event.dto.StaffDto;
import com.dat.event.entity.StaffEntity;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Override
    public boolean existsByStaffNo(String staffNo) {
        return staffRepository.existsByStaffNo(staffNo) > 0;
    }

    @Override
    public Page<StaffDto> findAll(final int page) {
        return staffRepository.findAll(PageRequest.of(page, Constants.PAGE_LIMIT, Sort.by("createdAt").descending())).map(staffMapper::toDTO);
    }

    @Override
    public void updateAdminFlag(final String staffNo, final boolean adminFlag) {
        final StaffEntity staffEntity = staffRepository.findByStaffNo(staffNo).orElseThrow();
        staffEntity.setAdminFlag(adminFlag);
        staffRepository.save(staffEntity);
    }
}
