/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.Constants;
import com.dat.event.common.constant.WebUrl;
import com.dat.event.common.exception.ResourceNotFoundException;
import com.dat.event.dto.StaffDto;
import com.dat.event.email.config.EmailProperties;
import com.dat.event.email.service.EmailService;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.service.*;
import jakarta.servlet.http.HttpSession;
import com.dat.event.dto.BookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;


/**
 * BookingController Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final EventService eventService;
    private final EventPlannerService eventPlannerService;
    private final BookingService bookingService;
    private final EventScheduleService eventScheduleService;
    private final EmailService emailService;
    private final EmailProperties emailProperties;
    private final StaffService staffService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping(WebUrl.CAFETERIA_BOOKING_URL + "/{id}/{name}")
    public String event_registration(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, HttpSession session, Model model) throws AccessDeniedException {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }

        Object isAdmin = session.getAttribute("adminFlag");
        if (!Boolean.TRUE.equals(isAdmin)) {
            throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
        }

        var eventDto = eventService.getEvent(eventId);
        if (!eventDto.getName().trim().equals(eventName.trim())) throw new ResourceNotFoundException();
        model.addAttribute("planner", eventPlannerService.getEventWithSchedule(eventId));

        model.addAttribute("accessories", bookingService.getAccessories());
        return "cafeteria-booking";
    }

    @PostMapping(WebUrl.CAFETERIA_BOOKING_URL)
    public ResponseEntity<Page<EventScheduleDto>> eventSchedule(@RequestParam(required = false, defaultValue = "") final String keyword,
                                                                @RequestParam(required = false, defaultValue = "0") final int page,
                                                                @RequestParam final Long eventId) {
        return ResponseEntity.ok(eventScheduleService.getScheduleById(eventId, keyword.isBlank() ? null : keyword, page, true));
    }

    @PostMapping(WebUrl.CHECK_BOOKED_URL)
    public ResponseEntity<?> checkBooked(@RequestParam Long scheduleId) {
        return ResponseEntity.ok(eventScheduleService.checkTimeAlreadyBooked(scheduleId));
    }

    @PostMapping(WebUrl.CAFETERIA_BOOKING_POST_URL)
    public ResponseEntity<?> cafeteria_booking(@RequestParam("scheduleId") Long scheduleId,
                                               @RequestParam("attendees") int attendees,
                                               @RequestParam(value = "accessories", required = false) List<Long> accessories,
                                               @RequestParam(value = "accessoriesName", required = false) List<String> accessoriesName,
                                               @RequestParam("purpose") String purpose,
                                               @RequestPart("signature") MultipartFile signature,
                                               @RequestParam("submitType") String submitType,
                                               @RequestParam("eventId") Long eventId,
                                               @RequestParam("scheduleDate") String scheduleDate,
                                               @RequestParam("eventName") String eventName, HttpSession session) {

        Map<String, String> response = new HashMap<>();
        if (session == null || session.getAttribute("staffNo") == null) {
            response.put("redirectUrl", contextPath + WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        String staffId = (String) session.getAttribute("staffNo");
        bookingService.makeBooking(scheduleId, attendees, accessories, purpose, signature, staffId, eventName);
        boolean isMailSend = sendEmail(attendees, accessoriesName, purpose, scheduleDate, eventName, staffId);

        String redirectUrl = switch (submitType) {
            case Constants.SAVE -> contextPath + WebUrl.EVENT_URL;
            case Constants.CONTINUE -> contextPath + WebUrl.CAFETERIA_BOOKING_URL.concat("/" + eventId + "/" + eventName);
            default -> throw new IllegalStateException("Unexpected submitType: " + submitType);
        };

        response.put("redirectUrl", redirectUrl);
        response.put("status", "success");
        response.put("message", isMailSend ? "Booking Successful and Mail Send Successful" : "Booking Successful  but Mail Send Fail");
        return ResponseEntity.ok(response);
    }

    @PostMapping(WebUrl.GET_BOOKING_SCHEDULE_URL)
    public ResponseEntity<BookingDto> getBookingSchedule(@RequestParam("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(bookingService.getBookingSchedule(scheduleId));
    }

    @PostMapping(WebUrl.EDIT_CAFETERIA_BOOKING_URL)
    public ResponseEntity<?> edit_cafeteria_booking(@RequestParam("bookingId") Long bookingId,
                                                    @RequestParam("attendees") int attendees,
                                                    @RequestParam(value = "accessories", required = false) List<Long> accessories,
                                                    @RequestParam("purpose") String purpose,
                                                    @RequestParam("eventId") Long eventId,
                                                    @RequestParam("eventName") String eventName,
                                                    @RequestParam("submitType") String submitType, HttpSession session){

        Map<String, String> response = new HashMap<>();
        if (session == null || session.getAttribute("staffNo") == null) {
            response.put("redirectUrl", contextPath + WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        String staffNo = (String) session.getAttribute("staffNo");
        bookingService.updateBooking(bookingId, attendees, accessories, purpose, staffNo);

        if (submitType.equals(Constants.SAVE)) {
            response.put("redirectUrl", contextPath + WebUrl.EVENT_URL);
            response.put("status", "success");
            response.put("message", "Booking Updated Success.");
        } else if (submitType.equals(Constants.CONTINUE)) {
            response.put("redirectUrl", contextPath + WebUrl.CAFETERIA_BOOKING_URL.concat("/") + eventId + "/" + eventName);
            response.put("status", "success");
            response.put("message", "Booking Updated Success.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(WebUrl.EVENT_BOOKING_URL)
    public String bookingList(HttpSession session) throws AccessDeniedException {
        if (session != null && session.getAttribute("staffNo") != null) {
            Object isAdmin = session.getAttribute("adminFlag");
            if (!Boolean.TRUE.equals(isAdmin)) {
                throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
            }
            return "booking-list";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.EVENT_BOOKING_URL)
    public ResponseEntity<Page<BookingDto>> bookingList(@RequestParam(required = false, defaultValue = "") final String keyword,
                                                        @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(bookingService.findAllBooking(keyword.isBlank() ? null : keyword, page));
    }

    @PostMapping(WebUrl.EVENT_BOOKING_APPROVE_URL)
    public ResponseEntity<Map<String, String>> approveForms(@RequestParam("bookingId") Long bookingId,
                                                            @RequestParam("approveReason") String reason,
                                                            @RequestParam(value = "formAction", required = false) String action,
                                                            HttpSession session) throws AccessDeniedException {

        Map<String, String> response = new HashMap<>();
        if (session != null && session.getAttribute("staffNo") != null) {
            Object isApproval = session.getAttribute("approval");
            if (!Boolean.TRUE.equals(isApproval)) {
                throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
            }
            bookingService.checkBookingANDApproverFlag(bookingId, (String) session.getAttribute("staffNo"));
            bookingService.approveBookingById(bookingId, (String) session.getAttribute("name"), reason, action);

            response.put("redirectUrl", contextPath.concat(WebUrl.EVENT_BOOKING_URL));
            response.put("status", "success");
            response.put("message", "approve".equalsIgnoreCase(action) ? " Approved Successfully." : "Rejected Successfully.");

            return ResponseEntity.ok(response);
        }
        response.put("redirectUrl", WebUrl.LOGIN_URL);
        response.put("status", "error");
        response.put("message", "No Permission to" + session.getAttribute("staffNo"));
        return ResponseEntity.ok(response);
    }

    private boolean sendEmail(int attendees, List<String> accessoriesName, String purpose, String scheduleDate, String eventName, String staffNo) {
        StaffDto staffDto = staffService.findByStaffNo(staffNo);
        try {
            ClassPathResource resource = new ClassPathResource(emailProperties.getTemplatePath());
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String requestAccessories = (accessoriesName == null || accessoriesName.isEmpty()) ? "None" : String.join(", ", accessoriesName);
            String body = template
                    .replace("{eventName}", eventName)
                    .replace("{date}", scheduleDate)
                    .replace("{attendees}", String.valueOf(attendees))
                    .replace("{accessories}", requestAccessories)
                    .replace("{purpose}", purpose)
                    .replace("{name}", staffDto.getName())
                    .replace("{team}", staffDto.getTeam())
                    .replace("{department}", staffDto.getDepartment());
            emailService.sendMail(staffDto.getEmail(), "Cafeteria Booking Request", body);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
