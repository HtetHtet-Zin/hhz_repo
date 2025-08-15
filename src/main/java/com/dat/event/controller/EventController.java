/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.common.excel.ExcelUtility;
import com.dat.event.dto.*;
import com.dat.event.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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

    @PostMapping(WebUrl.EVENT_CHECK_URL)
    public ResponseEntity<Map<String, Object>> checkEvent(@RequestParam("eventName") String eventName){
        Map<String, Object> response = new HashMap<>();
        EventDto eventDto = eventService.findByEventName(eventName);
        log.info("EventDto {}", eventDto);
        if (eventDto != null) {
            response.put("redirectUrl", "/club" + WebUrl.EVENT_CREATE_URL);
            response.put("status", "error");
            response.put("message", "Can't create the same event");
            return ResponseEntity.ok(response);
        }
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(WebUrl.EVENT_CREATE_URL)
    public ResponseEntity<Map<String, String>> createEvent(
            HttpSession session,
            @RequestPart("eventData") RequestEventPlanDto requestEventPlanDto,
            @RequestPart("eventPhoto") MultipartFile eventPhotoFile) {
        // step-1 create event, (name,description,createAt,createBy)
        // step-2 create schedule, (eventId,date,startTime,endTime,createAt,createBy,updateAt,updateBy)
        // step-3 create eventPlanner, (staffId,eventId,supportedMemberFlg,delFlg)
        Map<String, String> response = new HashMap<>();
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null ? session.getAttribute("staffNo").toString() : null;
        if (loginStaffNo == null) {
            response.put("redirectUrl", WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }

        EventDto eventDto = eventService.findByEventName(requestEventPlanDto.getEventName());
        if (eventDto != null) {
            response.put("redirectUrl", "/club" + WebUrl.EVENT_CREATE_URL);
            response.put("status", "error");
            response.put("message", "Can't create the same event");
            System.out.println("Can't create the same event");
            return ResponseEntity.ok(response);
        }

        EventDto savedDto = eventService.save(requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
        eventScheduleService.saveEventSchedule(savedDto, requestEventPlanDto, loginStaffNo);
        eventPlannerService.saveEventPlanner(savedDto, requestEventPlanDto, loginStaffNo);
        imageStorageService.saveImage(eventPhotoFile, savedDto.getName());

        response.put("redirectUrl", "/club" + WebUrl.EVENT_URL);
        response.put("status", "success");
        response.put("message", "Event created successfully");
        System.out.println("Event created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping(WebUrl.EVENT_EDIT_URL+"/{id}")
    public ModelAndView showEditEventPage(@PathVariable("id") Long eventId, HttpSession session) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return new ModelAndView("redirect:" + WebUrl.LOGIN_URL);
        }
        Boolean isAdmin = (Boolean) session.getAttribute("adminFlag");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return new ModelAndView("redirect:" + WebUrl.EVENT_URL);
        }
        var staffDtoList = staffService.findAll();
        var eventDto = eventService.getEvent(eventId);
        var eventScheduleDtoList =  eventScheduleService.getEventSchedule(eventId);
        var inChargePerson = eventPlannerService.getInChargePerson(eventId);
        var supportedMemberList = eventPlannerService.getSupportedMember(eventId);
        log.info("ssup {}", eventScheduleDtoList);
       // log.info("staff-list {}", staffDtoList);
        return new ModelAndView("event-edit-page")
                .addObject( "staffs", staffDtoList)
                .addObject( "eventId", eventDto.getEventId())
                .addObject("eventName",eventDto.getName())
                .addObject("description",eventDto.getDescription())
                .addObject("eventScheduleList",eventScheduleDtoList)
                .addObject("inChargePerson",inChargePerson.getName())
                .addObject("inChargePersonId",inChargePerson.getStaffId())
                .addObject("inChargePersonPlanId",inChargePerson.getPlannerId())
                .addObject("supportedMemberList",supportedMemberList);
    }

    @PostMapping(WebUrl.EVENT_EDIT_URL)
    public ResponseEntity<?> editEvent(
            HttpSession session,
            @RequestPart("eventData") UpdateEventPlanDto requestEventPlanDto,
            @RequestPart(value = "eventPhoto",required = false) MultipartFile eventPhotoFile) {

        log.info("RequestEventPlanner {}", requestEventPlanDto);
        log.info("eventPhoto {}", eventPhotoFile);
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null ? session.getAttribute("staffNo").toString() : null;
        log.info("loginStaffNo {}", loginStaffNo);
        EventDto eventDto = eventService.findById(requestEventPlanDto.getEventId());
        if (eventDto != null && loginStaffNo != null) {
            EventDto updateDto = eventService.update(eventDto.getEventId(), requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
            eventScheduleService.updateEventSchedule(updateDto, requestEventPlanDto, loginStaffNo);
            eventPlannerService.updateEventPlanner(updateDto, requestEventPlanDto, loginStaffNo);
            if (eventPhotoFile != null && !eventPhotoFile.isEmpty()) {
                imageStorageService.saveImage(eventPhotoFile, updateDto.getName());
            }else{
                imageStorageService.updateImage(eventDto.getName(),requestEventPlanDto.getEventName());
            }
        }
        return ResponseEntity.ok(requestEventPlanDto);
    }


    @GetMapping(WebUrl.EVENT_URL)
    public String view(HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            var eventList = eventService.findAll();
            var imageMap = imageStorageService.findEventImage(eventList.stream().map(EventDto::getName).toList());
            eventList.forEach(event -> {
                String eventPhoto = imageMap.getOrDefault(event.getName(), "default.jpg");
                event.setEventPhoto(eventPhoto);
            });
            model.addAttribute("eventList", eventList);
            return "event";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @GetMapping(WebUrl.EVENTS_URL)
    public String eventList(HttpSession session) {
        if (session != null && session.getAttribute("staffNo") != null) return "participant-list";
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.EVENTS_URL)
    public ResponseEntity<Page<EventStaffDto>> eventList(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(keyword.isBlank() ? eventRegistrationService.fetchEventStaffList(null, page) : eventRegistrationService.fetchEventStaffList(keyword, page));
    }

    @GetMapping(WebUrl.EVENT_REGISTRATION_URL + "/{id}")
    public String event_registration(@PathVariable("id") String eventId, HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            model.addAttribute("planner", eventPlannerService.getEventWithSchedule(Long.parseLong(eventId)));
            model.addAttribute("registeredSchedule", eventRegistrationService.getRegisteredSchedule(Long.valueOf(eventId), (Long) session.getAttribute("id")));
            model.addAttribute("allScheduleIds", eventScheduleService.getAllScheduleIdByEvent(Long.valueOf(eventId)));
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
    public ResponseEntity<Map<String, String>> handleSchedules(@RequestParam(required = false) List<Long> registeredScheduleIds, @RequestParam String eventId, HttpSession session, RedirectAttributes redirectAttributes) {
        Map<String, String> response = new HashMap<>();
        if (session == null) {
            response.put("redirectUrl", WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        Long staffId = (Long) session.getAttribute("id");
        List<String> conflicts = eventRegistrationService.checkDuplicateSchedule(staffId, registeredScheduleIds);
        if (!conflicts.isEmpty()) {
            response.put("redirectUrl", "/club" + WebUrl.EVENT_REGISTRATION_URL.concat("/") + eventId);
            response.put("status", "error");
            response.put("message", "Already Registered at this date and time - " + String.join(", ", conflicts));
            return ResponseEntity.ok(response);
        }
        int success = eventRegistrationService.registerEvent(staffId, registeredScheduleIds, Long.valueOf(eventId));
        if (success == 0) {
            response.put("redirectUrl", "/club" + WebUrl.EVENT_REGISTRATION_URL.concat("/") + eventId);
            response.put("status", "error");
            response.put("message", "Fail to Join");
        } else {
            response.put("redirectUrl", "/club" + WebUrl.EVENT_URL);
            response.put("status", "success");
            response.put("message", "Successfully Join");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(WebUrl.EVENT_STAFF_DOWNLOAD_URL)
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false, defaultValue = "") final String keyword) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=event_staff_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, ExcelUtility.EXCEL_FILE_TYPE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(eventRegistrationService.exportExcel(keyword));
    }

    @GetMapping(WebUrl.EVENT_DELETE_URL + "/{id}")
    public String deleteEvent(@PathVariable ("id") String eventId, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }
        Boolean isAdmin = (Boolean) session.getAttribute("adminFlag");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return "redirect:" + WebUrl.EVENT_URL;
        }

        eventScheduleService.deleteSchedule(Long.valueOf(eventId));
        eventPlannerService.deletePlanner(Long.parseLong(eventId));
        eventService.deleteEvent(Long.valueOf(eventId));
        log.info("Delete Event - " + eventId + " - By " + session.getAttribute("staffNo").toString());
        redirectAttributes.addFlashAttribute("message", "Delete Success");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:" + WebUrl.EVENT_URL;
    }

}
