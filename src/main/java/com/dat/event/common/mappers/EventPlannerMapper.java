package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventPlannerDto;
import com.dat.event.entity.EventPlannerEntity;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface EventPlannerMapper extends BaseMapper<EventPlannerDto, EventPlannerEntity>{
}
