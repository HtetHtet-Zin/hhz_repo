/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.EventDto;

import java.util.List;

/**
 * EventService Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventService {

    EventDto save(EventDto dto);

    List<EventDto> findAll();

    EventDto findById(Long id);
}
