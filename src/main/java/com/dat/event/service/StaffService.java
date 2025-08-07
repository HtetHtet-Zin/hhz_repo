package com.dat.event.service;

import com.dat.event.dto.StaffDto;

import java.util.List;

public interface StaffService {

    boolean existsByStaffNo(String staffNo);

    List<StaffDto> findAll();
}
