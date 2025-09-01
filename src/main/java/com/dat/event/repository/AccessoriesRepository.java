package com.dat.event.repository;

import com.dat.event.entity.AccessoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessoriesRepository extends JpaRepository<AccessoriesEntity, Long> {
}
