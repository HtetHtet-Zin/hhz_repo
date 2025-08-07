package com.dat.event.repository;

import com.dat.event.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    @Query(value = "SELECT COUNT(*) FROM tbl_staff WHERE staff_no = :staffNo", nativeQuery = true)
    long existsByStaffNo(@Param("staffNo") String staffNo);




}
