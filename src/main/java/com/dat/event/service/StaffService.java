package com.dat.event.service;

import com.dat.event.dto.StaffDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    boolean existsByStaffNo(String staffNo, HttpSession session);

    Page<StaffDto> findAll(int page);

    Page<StaffDto> findAll(String keyword, int page);

    List<StaffDto> findAll();

    void updateAdminFlag(String staffNo, boolean adminFlag);

    void updateApproverFlag(String staffNo, boolean approverFlag);

    List<StaffDto> birthdayStaffList();

    StaffDto findByStaffNo(String staffNo);
}
