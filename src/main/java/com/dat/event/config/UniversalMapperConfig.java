/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.config;

import com.dat.event.dto.EventDto;
import com.dat.event.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

import java.util.List;

/**
 * UniversalMapperConfig Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Mapper(config = GlobalMapperConfig.class)
public interface UniversalMapperConfig {

    EventEntity toEntity(EventDto dto);

    EventDto toDTO(EventEntity entity);

    List<EventDto> toDtoList(List<EventEntity> entityList);

    List<EventEntity> toEntityList(List<EventDto> dtoList);

}
@MapperConfig(componentModel = "spring")
interface GlobalMapperConfig {}