package com.dat.event.service.impl;

import com.dat.event.common.mappers.StaffMapper;
import com.dat.event.dto.StaffDto;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<StaffDto> findAll() {
        return staffMapper.toDtoList(staffRepository.findAll());
    }
}
