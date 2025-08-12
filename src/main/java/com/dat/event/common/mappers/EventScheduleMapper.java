package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.entity.EventEntity;
import com.dat.event.entity.EventScheduleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class, componentModel = "spring")
public abstract class EventScheduleMapper implements BaseMapper<EventScheduleDto, EventScheduleEntity> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "event", expression = "java(mapEvent(dto.getEventDto()))")
    public abstract EventScheduleEntity toEntity(EventScheduleDto dto);

    protected EventEntity mapEvent(EventDto eventDto) {
        if (eventDto == null || eventDto.getEventId() == null) {
            return null;
        }
        return entityManager.getReference(EventEntity.class, eventDto.getEventId());
    }
}
