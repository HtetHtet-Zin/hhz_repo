/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.common.excel.ExcelUtility;
import com.dat.event.common.exception.ResourceNotFoundException;
import com.dat.event.dto.*;
import com.dat.event.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
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
    private final BookingService bookingService;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @GetMapping(WebUrl.EVENT_CREATE_URL)
    public String showCreateEventPage(HttpSession session, Model model) throws AccessDeniedException {
        if (session != null && session.getAttribute("staffNo") != null) {
            Object isAdmin = session.getAttribute("adminFlag");
            if (!Boolean.TRUE.equals(isAdmin)) {
                throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
            }
            model.addAttribute("staffs", staffService.findAll());
            return "event-create-page";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.EVENT_CHECK_URL)
    public ResponseEntity<Map<String, Object>> checkEvent(@RequestParam("eventName") String eventName) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", eventService.findByEventName(eventName) != null ? "error" : "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping(WebUrl.EVENT_CREATE_URL)
    public ResponseEntity<?> createEvent(
            HttpSession session, RedirectAttributes redirectAttributes,
            @RequestPart("eventData") RequestEventPlanDto requestEventPlanDto,
            @RequestPart("eventPhoto") MultipartFile eventPhotoFile) {
        // step-1 create event, (name,description,createAt,createBy)
        // step-2 create schedule, (eventId,date,startTime,endTime,createAt,createBy,updateAt,updateBy)
        // step-3 create eventPlanner, (staffId,eventId,supportedMemberFlg,delFlg)
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null ? session.getAttribute("staffNo").toString() : null;
        Map<String, String> response = new HashMap<>();

        if (loginStaffNo == null) {
            response.put("redirectUrl", WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);

        }

        EventDto savedDto = eventService.save(requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
        eventScheduleService.saveEventSchedule(savedDto, requestEventPlanDto, loginStaffNo);
        eventPlannerService.saveEventPlanner(savedDto, requestEventPlanDto, loginStaffNo);
        imageStorageService.saveImage(eventPhotoFile, savedDto.getName(), true);

        if (requestEventPlanDto.getEventLocation().equals("OFFICE")) {
            response.put("redirectUrl", contextPath.concat(WebUrl.CAFETERIA_BOOKING_URL).concat("/").concat(savedDto.getEventId().toString()).concat("/").concat(savedDto.getName()));
            response.put("status", "success");
            response.put("message", "Event created successfully");
            return ResponseEntity.ok(response);
        }

        response.put("redirectUrl", contextPath.concat(WebUrl.EVENT_URL));
        response.put("status", "success");
        response.put("message", "Event created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping(WebUrl.EVENT_EDIT_URL + "/{id}/{name}")
    public ModelAndView showEditEventPage(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, HttpSession session) throws AccessDeniedException {
        if (session == null || session.getAttribute("staffNo") == null) {
            return new ModelAndView("redirect:" + WebUrl.LOGIN_URL);
        }
        Boolean isAdmin = (Boolean) session.getAttribute("adminFlag");
        if (!Boolean.TRUE.equals(isAdmin)) {
            throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
        }
        var eventDto = eventService.getEvent(eventId);
        if (!eventDto.getName().equals(eventName)) throw new ResourceNotFoundException();
        var staffDtoList = staffService.findAll();
        var eventScheduleDtoList = eventScheduleService.getEventSchedule(eventId);
        var inChargePerson = eventPlannerService.getInChargePerson(eventId);
        var supportedMemberList = eventPlannerService.getSupportedMember(eventId);

        //log.info("staff-list {}", staffDtoList);
        return new ModelAndView("event-edit-page")
                .addObject("staffs", staffDtoList)
                .addObject("eventId", eventDto.getEventId())
                .addObject("eventName", eventDto.getName())
                .addObject("description", eventDto.getDescription())
                .addObject("eventScheduleList", eventScheduleDtoList)
                .addObject("inChargePerson", inChargePerson.getName())
                .addObject("inChargePersonId", inChargePerson.getStaffId())
                .addObject("inChargePersonPlanId", inChargePerson.getPlannerId())
                .addObject("supportedMemberList", supportedMemberList);
    }

    @PostMapping(WebUrl.EVENT_EDIT_URL)
    public ResponseEntity<?> editEvent(HttpSession session,
            @RequestPart("eventData") UpdateEventPlanDto requestEventPlanDto,
            @RequestPart(value = "eventPhoto", required = false) MultipartFile eventPhotoFile) {
        Map<String, String> response = new HashMap<>();
        String loginStaffNo = session != null && session.getAttribute("staffNo") != null ? session.getAttribute("staffNo").toString() : null;
        EventDto eventDto = eventService.findById(requestEventPlanDto.getEventId());

        if (eventDto != null && loginStaffNo != null) {
            EventDto updateDto = eventService.update(eventDto.getEventId(), requestEventPlanDto.getEventName(), requestEventPlanDto.getDescription(), eventPhotoFile, loginStaffNo);
            eventScheduleService.updateEventSchedule(updateDto, requestEventPlanDto, loginStaffNo);
            eventPlannerService.updateEventPlanner(updateDto, requestEventPlanDto, loginStaffNo);
            if (eventPhotoFile != null && !eventPhotoFile.isEmpty()) {
                imageStorageService.saveImage(eventPhotoFile, updateDto.getName(), true);
            } else if (!eventDto.getName().equals(requestEventPlanDto.getEventName())){
                    imageStorageService.updateImage(eventDto.getName(), requestEventPlanDto.getEventName());
                    //if event name change also change event name in booking table
                  List<Long>scheduleIds =  eventScheduleService.getAllScheduleIdByEvent(updateDto.getEventId());
                  bookingService.changeEventName(scheduleIds,requestEventPlanDto.getEventName());
            }
            if (requestEventPlanDto.getEventLocation().equals("OFFICE")) {
                response.put("redirectUrl", contextPath.concat(WebUrl.CAFETERIA_BOOKING_URL).concat("/").concat(updateDto.getEventId().toString()).concat("/").concat(updateDto.getName()));
                response.put("status", "success");
                response.put("message", "Event updated successfully");
                return ResponseEntity.ok(response);
            }
        }

        response.put("redirectUrl", contextPath.concat(WebUrl.EVENT_URL));
        response.put("status", "success");
        response.put("message", "Event updated successfully");
        return ResponseEntity.ok(response);
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
    public String eventList(HttpSession session, Model model) {

        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }

        model.addAttribute("events", eventService.findAll());
        return "participant-list";
    }

    @PostMapping(WebUrl.EVENTS_URL)
    public ResponseEntity<Page<EventStaffDto>> eventList(@RequestParam(required = false, defaultValue = "") final String eventName,
                                                         @RequestParam(required = false, defaultValue = "") final String keyword,
                                                         @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(eventRegistrationService.fetchEventStaffList(eventName.isBlank() ? null : eventName, keyword.isBlank() ? null : keyword, page));
    }

    @GetMapping(WebUrl.EVENT_REGISTRATION_URL + "/{id}/{name}")
    public String event_registration(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, HttpSession session, Model model) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }
        var eventDto = eventService.getEvent(eventId);
        if (!eventDto.getName().trim().equals(eventName.trim())) throw new ResourceNotFoundException();

        model.addAttribute("planner", eventPlannerService.getEventWithSchedule(eventId));
        model.addAttribute("registeredSchedule", eventRegistrationService.getRegisteredSchedule(eventId, (Long) session.getAttribute("id")));
        model.addAttribute("allScheduleIds", eventScheduleService.getAllScheduleIdByEvent(eventId));
        return "event-registration";
    }

    @PostMapping(WebUrl.EVENT_REGISTRATION_URL)
    public ResponseEntity<Page<EventScheduleDto>> eventSchedule(@RequestParam(required = false, defaultValue = "") final String keyword,
                                                                @RequestParam(required = false, defaultValue = "0") final int page,
                                                                @RequestParam final Long eventId) {
        return ResponseEntity.ok(eventScheduleService.getScheduleById(eventId, keyword.isBlank() ? null : keyword, page, false));
    }

    @PostMapping(WebUrl.EVENT_REGISTRATION_POST_URL)
    public ResponseEntity<Map<String, String>> handleSchedules(@RequestParam(required = false) List<Long> registeredScheduleIds,
                                                               @RequestParam Long eventId, @RequestParam String eventName,
                                                               @RequestParam boolean isNew, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        if (session == null) {
            response.put("redirectUrl", contextPath + WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        Long staffId = (Long) session.getAttribute("id");
        List<String> conflicts = eventRegistrationService.checkDuplicateSchedule(staffId, registeredScheduleIds);
        String url = WebUrl.EVENT_REGISTRATION_URL.concat("/").concat(eventId.toString());
        if (!conflicts.isEmpty()) {
            response.put("redirectUrl", contextPath + url.concat("/").concat(eventName));
            response.put("status", "error");
            response.put("message", "Already Registered at this date and time - " + String.join(", ", conflicts));
            return ResponseEntity.ok(response);
        }
        int success = eventRegistrationService.registerEvent(staffId, registeredScheduleIds, eventId);
        if (success == 0) {
            response.put("redirectUrl", contextPath + url.concat("/").concat(eventName));
            response.put("status", "error");
            response.put("message", isNew ? "Fail to Participate." : "Fail to Update.");
        } else {
            response.put("redirectUrl", contextPath.concat(WebUrl.EVENT_URL));
            response.put("status", "success");
            response.put("message", isNew ? "Successfully Participate." : "Successfully Update.");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(WebUrl.EVENT_STAFF_DOWNLOAD_URL)
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false, defaultValue = "") final String keyword,
                                              @RequestParam(required = false, defaultValue = "") final String eventName) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=event_staff_report.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, ExcelUtility.EXCEL_FILE_TYPE);
        return ResponseEntity.ok()
                .headers(headers)
                .body(eventRegistrationService.exportExcel(keyword, eventName));
    }

    @GetMapping(WebUrl.EVENT_DELETE_URL + "/{id}/{name}")
    public String deleteEvent(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }
        Boolean isAdmin = (Boolean) session.getAttribute("adminFlag");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return "redirect:" + WebUrl.EVENT_URL;
        }
        var eventDto = eventService.getEvent(eventId);
        if (!eventDto.getName().equals(eventName)) throw new ResourceNotFoundException();
        eventScheduleService.deleteScheduleAndBooking(eventId);
        eventPlannerService.deletePlanner(eventId);
        eventService.deleteEvent(eventId);
        log.info("Delete Event - {} - By {} ", eventId, session.getAttribute("staffNo").toString());
        redirectAttributes.addFlashAttribute("message", "Delete Success");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:" + WebUrl.EVENT_URL;
    }

}
