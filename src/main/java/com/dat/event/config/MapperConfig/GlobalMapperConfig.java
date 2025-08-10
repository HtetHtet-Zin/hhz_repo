/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.config.MapperConfig;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * GlobalMapperConfig Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface GlobalMapperConfig {
}