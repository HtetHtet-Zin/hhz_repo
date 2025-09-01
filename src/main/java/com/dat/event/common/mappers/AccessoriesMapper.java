package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.AccessoriesDto;
import com.dat.event.entity.AccessoriesEntity;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface AccessoriesMapper extends BaseMapper<AccessoriesDto, AccessoriesEntity>{
}
