/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.mappers;

import com.dat.event.config.MapperConfig.GlobalMapperConfig;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.StaffDto;
import com.dat.event.entity.EventEntity;
import com.dat.event.entity.StaffEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * StaffMapper Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Mapper(config = GlobalMapperConfig.class)
public interface StaffMapper extends BaseMapper<StaffDto, StaffEntity> {

}
