package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventPlannerDto;
import com.dat.event.entity.EventEntity;
import com.dat.event.entity.EventPlannerEntity;
import com.dat.event.entity.StaffEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public abstract class EventPlannerMapper implements BaseMapper<EventPlannerDto, EventPlannerEntity> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "event", expression = "java(mapEvent(dto.getEventId()))")
    @Mapping(target = "staff", expression = "java(mapStaff(dto.getStaffId()))")
    @Override
    public abstract EventPlannerEntity toEntity(EventPlannerDto dto);

    protected EventEntity mapEvent(Long eventId) {
        if (eventId == null) {
            return null;
        }
        return entityManager.getReference(EventEntity.class, eventId);
    }

    protected StaffEntity mapStaff(Long staffId) {
        if (staffId == null) {
            return null;
        }
        return entityManager.getReference(StaffEntity.class, staffId);
    }
}

