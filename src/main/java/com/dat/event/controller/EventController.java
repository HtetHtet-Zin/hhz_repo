/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventStaffDto;
import com.dat.event.dto.RequestEventPlanDto;
import com.dat.event.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalTime;
import java.util.Objects;


/**
 * EventController Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo, Kaung Khant Ko
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRegistrationService eventRegistrationService;
    private final StaffService staffService;
    private final EventScheduleService eventScheduleService;
    private final EventPlannerService eventPlannerService;

    @GetMapping(WebUrl.EVENT_CREATE_URL)
    public ModelAndView showCreateEventPage() {
        var staffDtoList = staffService.findAll();
        log.info("staff-list {}", staffDtoList);
        return new ModelAndView("event-create-page", "staffs", staffDtoList);
    }

    @PostMapping(WebUrl.EVENT_CREATE_URL)
    public ResponseEntity<?> createEvent(
            HttpSession session,
            @RequestPart("eventData") RequestEventPlanDto requestEventPlanDto,
            @RequestPart("eventPhoto") MultipartFile eventPhotoFile) {
        // step-1 create event, (name,description,createAt,createBy)
        // step-2 create schedule, (eventId,date,startTime,endTime,createAt,createBy,updateAt,updateBy)
        // step-3 create eventPlanner, (staffId,eventId,supportedMemberFlg,delFlg)

        log.info("RequestEventPlanner {}", requestEventPlanDto);
        log.info("eventPhoto {}", eventPhotoFile);
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null
                ? session.getAttribute("staffNo").toString()
                : null;
        log.info("loginStaffNo {}", loginStaffNo);
        EventDto eventDto = eventService.findByEventName(requestEventPlanDto.getEventName());
        if (eventDto == null && loginStaffNo != null) {
            EventDto savedDto = eventService.save(requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
            log.info("Event Saved. {}",savedDto.getEventId());
            eventScheduleService.save(savedDto, requestEventPlanDto, loginStaffNo);
            eventPlannerService.saveEventPlanner(savedDto, requestEventPlanDto, loginStaffNo);
        }

        return ResponseEntity.ok(requestEventPlanDto);
    }


    @GetMapping(WebUrl.EVENT_URL)
    public String view(HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            model.addAttribute("eventList",eventService.findAll());
            return "event";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @GetMapping(WebUrl.EVENTS_URL)
    public String eventList(HttpSession session) {
        if (session != null && session.getAttribute("staffNo") != null) return "event-list";
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.EVENTS_URL)
    public ResponseEntity<Page<EventStaffDto>> eventList(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(keyword.isBlank() ? eventRegistrationService.fetchEventStaffList(null, page) : eventRegistrationService.fetchEventStaffList(keyword, page));
    }


}
