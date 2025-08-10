package com.dat.event.repository;

import com.dat.event.entity.EventPlannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventPlannerRepository extends JpaRepository<EventPlannerEntity,Long> {
}
