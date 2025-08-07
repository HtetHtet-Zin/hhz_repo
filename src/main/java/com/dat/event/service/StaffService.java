package com.dat.event.service;

import com.dat.event.dto.StaffDto;
import org.springframework.data.domain.Page;

import java.util.List;

import jakarta.servlet.http.HttpSession;

public interface StaffService {

    boolean existsByStaffNo(String staffNo, HttpSession session);

    Page<StaffDto> findAll(int page);

    void updateAdminFlag(String staffNo, boolean adminFlag);
}
