package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.entity.EventScheduleEntity;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface EventScheduleMapper extends BaseMapper<EventScheduleDto, EventScheduleEntity> {
}
