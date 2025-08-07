package com.dat.event.service;

import com.dat.event.dto.StaffDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    boolean existsByStaffNo(String staffNo);

    Page<StaffDto> findAll(int page);

    void updateAdminFlag(String staffNo, boolean adminFlag);
}
