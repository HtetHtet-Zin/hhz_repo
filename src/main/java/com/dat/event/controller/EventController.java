/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.dto.EventDto;
import com.dat.event.dto.EventScheduleDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private final EventScheduleService eventScheduleService;
    private final StaffService staffService;
    private final EventPlannerService eventPlannerService;
    private final ImageStorageService imageStorageService;

    @GetMapping(WebUrl.EVENT_CREATE_URL)
    public String showCreateEventPage(HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            model.addAttribute("staffs", staffService.findAll());
            return "event-create-page";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
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
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null ? session.getAttribute("staffNo").toString() : null;
        log.info("loginStaffNo {}", loginStaffNo);
        EventDto eventDto = eventService.findByEventName(requestEventPlanDto.getEventName());
        if (eventDto == null && loginStaffNo != null) {
            EventDto savedDto = eventService.save(requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
            eventScheduleService.saveEventSchedule(savedDto, requestEventPlanDto, loginStaffNo);
            eventPlannerService.saveEventPlanner(savedDto, requestEventPlanDto, loginStaffNo);
            imageStorageService.saveImage(eventPhotoFile,savedDto.getName());
        }

        return ResponseEntity.ok(requestEventPlanDto);
    }


    @GetMapping(WebUrl.EVENT_URL)
    public String view(HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            model.addAttribute("eventList", eventService.findAll());
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

    @GetMapping(WebUrl.EVENT_REGISTRATION_URL + "/{id}")
    public String event_registration(@PathVariable("id") String eventId, HttpSession session,Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            model.addAttribute("planner" ,eventPlannerService.getEventWithSchedule(Long.parseLong(eventId)));
            model.addAttribute("registeredSchedule" , eventRegistrationService.getRegisteredSchedule(Long.valueOf(eventId), (Long) session.getAttribute("id")));
            model.addAttribute("allScheduleIds" , eventScheduleService.getAllScheduleIdByEvent(Long.valueOf(eventId)));
            return "event-registration";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.EVENT_REGISTRATION_URL)
    public ResponseEntity<Page<EventScheduleDto>> eventSchedule(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page,
                                                                @RequestParam final Long eventId) {
        return ResponseEntity.ok(keyword.isBlank() ? eventScheduleService.getScheduleById(eventId, null, page) : eventScheduleService.getScheduleById(eventId, keyword, page));
    }

    @PostMapping("/register-event-schedule")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleSchedules(@RequestParam(required = false) List<Long> registeredScheduleIds, @RequestParam String eventId  ,HttpSession session, RedirectAttributes redirectAttributes) {
        Map<String, String> response = new HashMap<>();
        if (session == null) {
            response.put("redirectUrl", "/club/login");
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        Long staffId = (Long) session.getAttribute("id");
        List<String> conflicts = eventRegistrationService.checkDuplicateSchedule(staffId, registeredScheduleIds);
        if (!conflicts.isEmpty()) {
            response.put("redirectUrl", "/club/event-registration/" + eventId);
            response.put("status", "error");
            response.put("message", "Already Registered at this date and time - " + String.join(", ", conflicts));
            return ResponseEntity.ok(response);
        }
        int success = eventRegistrationService.registerEvent(staffId, registeredScheduleIds, Long.valueOf(eventId));
        if (success == 0) {
            response.put("redirectUrl", "/club/event-registration/" + eventId);
            response.put("status", "error");
            response.put("message", "Register Fail");
        } else {
            response.put("redirectUrl", "/club/event");
            response.put("status", "success");
            response.put("message", "Register Success");
        }
        return ResponseEntity.ok(response);
    }


}
