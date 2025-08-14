package com.dat.event.repository;

import com.dat.event.entity.StaffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByStaffNo(String staffNo);

    @Query(value = "SELECT COUNT(*) FROM tbl_staff WHERE staff_no = :staffNo", nativeQuery = true)
    Long existsByStaffNo(@Param("staffNo") String staffNo);

    Page<StaffEntity> findByNameContainingIgnoreCaseOrStaffNoContainingIgnoreCase(
            String name, String staffNo, Pageable pageable);


    @Query(value = "SELECT * FROM tbl_staff WHERE MONTH(dob) = :month",nativeQuery = true)
    List<StaffEntity> birthdayStaff(@Param("month") int month);

    @Query(value = "SELECT * FROM tbl_staff WHERE staff_id = :id",nativeQuery = true)
    StaffEntity findByStaff(@Param("id")Long id);
}
