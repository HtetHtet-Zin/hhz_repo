/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.mappers.EventMapper;
import com.dat.event.dto.EventDto;
import com.dat.event.entity.EventEntity;
import com.dat.event.entity.EventRegistrationEntity;
import com.dat.event.entity.EventScheduleEntity;
import com.dat.event.repository.EventPlannerRepository;
import com.dat.event.repository.EventRegistrationRepository;
import com.dat.event.repository.EventRepository;
import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.service.EventScheduleService;
import com.dat.event.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final EventMapper eventMapper;

    @Override
    public EventDto save(String eventName, String description, MultipartFile file, String staffNo) {
        return eventMapper.toDTO(repository.save(eventMapper.toEntity(EventDto.builder()
                .name(eventName)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(staffNo)
                .delFlag(false)
                .build())));
    }

    @Override
    public EventDto update(Long eventId, String eventName, String description, MultipartFile file, String staffNo) {
        return eventMapper.toDTO(repository.save(eventMapper.toEntity(EventDto.builder()
                .eventId(eventId)
                .name(eventName)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(staffNo)
                .delFlag(false)
                .build())));
    }

    @Override
    public EventDto getEvent(long eventId) {
        return eventMapper.toDTO(repository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId)));
    }

    @Override
    public boolean delete(Long eventId) {
        try {
            repository.deleteById(eventId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.info("error");
            return false;
        }
    }

    @Override
    public List<EventDto> findAll() {
        return eventMapper.toDtoList(repository.findAll());
    }

    @Override
    public EventDto findById(final Long id) {
        return eventMapper.toDTO(repository.findById(id).orElseThrow());
    }

    @Override
    public EventDto findByEventName(String eventName) {
        return eventMapper.toDTO(repository.findByName(eventName));
    }

    @Transactional
    @Override
    public void deleteEvent(Long eventId) {
        repository.deleteById(eventId);
    }
}
