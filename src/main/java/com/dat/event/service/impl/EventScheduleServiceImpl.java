/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.service.EventScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * EventScheduleServiceImpl Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventScheduleServiceImpl implements EventScheduleService {

    private final EventScheduleRepository eventScheduleRepository;

}
