/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.mappers.EventMapper;
import com.dat.event.dto.EventDto;
import com.dat.event.repository.EventRepository;
import com.dat.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * EventServiceImpl Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper eventMapper;

    @Override
    public EventDto save(final EventDto dto) {
        return eventMapper.toDTO(repository.save(eventMapper.toEntity(dto)));
    }

    @Override
    public List<EventDto> findAll() {
        return eventMapper.toDtoList(repository.findAll());
    }

    @Override
    public EventDto findById(final Long id) {
        return eventMapper.toDTO(repository.findById(id).orElseThrow());
    }

}
