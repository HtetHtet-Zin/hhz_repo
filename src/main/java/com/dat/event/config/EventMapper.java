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

import java.util.List;

/**
 * EventMapper Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Mapper(config = GlobalMapperConfig.class)
public interface EventMapper {

    EventEntity toEntity(EventDto dto);

    EventDto toDTO(EventEntity entity);

    List<EventDto> toDtoList(List<EventEntity> entityList);

    List<EventEntity> toEntityList(List<EventDto> dtoList);
}
