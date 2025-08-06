package com.dat.event.service.impl;

import com.dat.event.repository.StaffRepository;
import com.dat.event.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public boolean existsByStaffNo(String staffNo) {
        return staffRepository.existsByStaffNo(staffNo) > 0;
    }
}
