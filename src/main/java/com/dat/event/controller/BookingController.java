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
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.service.*;
import jakarta.servlet.http.HttpSession;
import com.dat.event.dto.BookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping(WebUrl.CAFETERIA_BOOKING_URL + "/{id}/{name}")
    public String event_registration(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, HttpSession session, Model model) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
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
    public ResponseEntity<?> cafeteria_booking( @RequestParam("scheduleId") Long scheduleId,
                                     @RequestParam("attendees") int attendees,
                                     @RequestParam(value = "accessories", required = false) List<Long> accessories,
                                     @RequestParam("purpose") String purpose,
                                     @RequestPart("signature") MultipartFile signature,
                                     @RequestParam("submitType") String submitType,
                                     @RequestParam("eventId") Long eventId,
                                     @RequestParam("eventName") String eventName, HttpSession session) {

        System.out.println("schedule Id - " + scheduleId);
        System.out.println("attendees - " + attendees);
        System.out.println("accessories - " + accessories);
        System.out.println("purpose - " + purpose);
        System.out.println("eventId - " + eventId);
        System.out.println("eventName - " + eventName);
        System.out.println("signature - " + signature.getOriginalFilename());

        Map<String, String> response = new HashMap<>();
        if (session == null || session.getAttribute("staffNo") == null) {
            response.put("redirectUrl", contextPath + WebUrl.LOGIN_URL);
            response.put("status", "error");
            response.put("message", "Session has expired. Please log in again.");
            return ResponseEntity.ok(response);
        }
        String staffId = (String) session.getAttribute("staffNo");
        //bookingService.makeBooking(scheduleId, attendees, accessories, purpose, signature, staffId, eventName);

        if (submitType.equals(Constants.SAVE)) {
            response.put("redirectUrl", contextPath + WebUrl.EVENT_URL);
            response.put("status", "success");
            response.put("message", "Booking Success.");
        } else if (submitType.equals(Constants.CONTINUE)) {
            response.put("redirectUrl", contextPath + WebUrl.CAFETERIA_BOOKING_URL.concat("/") + eventId + "/" + eventName);
            response.put("status", "success");
            response.put("message", "Booking Success.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(WebUrl.EVENT_BOOKING_URL)
    public String bookingList(HttpSession session, Model model) throws AccessDeniedException {
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
    public ResponseEntity<Page<BookingDto>> bookingList( @RequestParam(required = false, defaultValue = "") final String keyword,
                                                         @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(bookingService.findAllBooking(keyword.isBlank() ? null : keyword, page));
    }

    ///  multiple approve
    @PostMapping(WebUrl.EVENT_BOOKING_APPROVE_URL)
    public ResponseEntity<Map<String, String>> approveForms(@RequestParam("bookingId") Long bookingId,
                                                            @RequestParam("approveReason") String reason,
                                                            @RequestParam(value = "formAction",required = false) String action,
                                                            HttpSession session,
                                                            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        Map<String, String> response = new HashMap<>();
            if (session != null && session.getAttribute("staffNo") != null) {
                Object isApproval = session.getAttribute("approval");
                if (!Boolean.TRUE.equals(isApproval)) {
                    throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
                }
                System.out.println("syarMyo"+action);
                bookingService.checkBookingANDApproverFlag(bookingId, (String) session.getAttribute("staffNo"));
                bookingService.approveBookingById(bookingId, (String) session.getAttribute("name"), reason, action);

                    response.put("redirectUrl", contextPath.concat(WebUrl.EVENT_BOOKING_URL));
                    response.put("status", "success");
                    response.put("message", "approve".equalsIgnoreCase(action) ? " Approved Successfully." : "Rejected Successfully.");

            return ResponseEntity.ok(response);
        }
        response.put("redirectUrl", WebUrl.LOGIN_URL);
        response.put("status", "error");
        response.put("message", "No Permission to"+ session.getAttribute("staffNo"));
        return ResponseEntity.ok(response);
    }
}
