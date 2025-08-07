package com.dat.event.repository;

import com.dat.event.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByStaffNo(String staffNo);

    @Query(value = "SELECT COUNT(*) FROM tbl_staff WHERE staff_no = :staffNo", nativeQuery = true)
    Long existsByStaffNo(@Param("staffNo") String staffNo);

}
