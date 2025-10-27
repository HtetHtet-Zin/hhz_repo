/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.mappers;

import com.dat.event.common.exception.BadRequestException;
import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventDto;
import com.dat.event.entity.EventEntity;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * EventMapper Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Mapper(config = GlobalMapperConfig.class)
public interface EventMapper extends BaseMapper<EventDto, EventEntity> {

    @Override
    @Mapping(target = "location", qualifiedByName = "parseLocation")
    EventEntity toEntity(EventDto dto);

    @Override
    @InheritInverseConfiguration
    @Mapping(target = "location", expression = "java(entity.getLocation() == 0 ? \"Other Location\" : \"Cafeteria Area\")")
    EventDto toDTO(EventEntity entity);

    @Named("parseLocation")
    default int parseLocation(String location) {
        if(location == null){
            throw new BadRequestException("Location field is required.");
        }

        return switch (location){
            case "OFFICE" -> 1;
            case "OTHER" -> 0;
            default -> throw new BadRequestException("Cannot parse location (" + location + ").");
        };
    }
}
