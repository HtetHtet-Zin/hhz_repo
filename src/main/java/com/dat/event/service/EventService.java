/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.EventDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * EventService Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
public interface EventService {

    EventDto save(String eventName, String description, MultipartFile file, String staffNo);

    EventDto update(Long eventId, String eventName, String description, MultipartFile file, String staffNo);

    EventDto getEvent(long eventId);

    boolean delete(Long eventId);

    List<EventDto> findAll();

    EventDto findById(Long id);

    EventDto findByEventName(String eventName);

    void deleteEvent(Long eventId);

}
