/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.config.UniversalMapperConfig;
import com.dat.event.dto.EventDto;
import com.dat.event.repository.EventRepository;
import com.dat.event.service.EventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final UniversalMapperConfig eventMapper;

    @Override
    public EventDto save(EventDto dto) {
        return eventMapper.toDTO(repository.save(eventMapper.toEntity(dto)));
    }

    @Override
    public List<EventDto> findAll() {
        return eventMapper.toDtoList(repository.findAll());
    }

    @Override
    public EventDto findById(Long id) {
        return eventMapper.toDTO(repository.findById(id).orElseThrow());
    }

    @PostConstruct
    public void test(){
        System.out.println("Testing");
        System.out.println(findAll());
        System.out.println(findById(1L));
    }

}
