/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventDto;
import com.dat.event.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * EventMapper Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Mapper(config = GlobalMapperConfig.class)
public interface EventMapper extends BaseMapper<EventDto, EventEntity> {

}
