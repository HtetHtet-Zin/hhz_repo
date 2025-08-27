/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.common.exception.ResourceNotFoundException;
import com.dat.event.service.EventPlannerService;
import com.dat.event.service.EventService;
import jakarta.servlet.http.HttpSession;
import com.dat.event.common.constant.WebUrl;
import com.dat.event.dto.BookingDto;
import com.dat.event.dto.EventStaffDto;
import com.dat.event.dto.StaffDto;
import com.dat.event.service.BookingService;
import com.dat.event.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping(WebUrl.CAFETERIA_BOOKING_URL + "/{id}/{name}")
    public String event_registration(@PathVariable("id") Long eventId, @PathVariable("name") String eventName, HttpSession session, Model model) {
        if (session == null || session.getAttribute("staffNo") == null) {
            return "redirect:" + WebUrl.LOGIN_URL;
        }

        var eventDto = eventService.getEvent(eventId);
        if (!eventDto.getName().trim().equals(eventName.trim())) throw new ResourceNotFoundException();

        model.addAttribute("planner", eventPlannerService.getEventWithSchedule(eventId));

        return "cafeteria-booking";
    }

    @PostMapping(WebUrl.CAFETERIA_BOOKING_POST_URL)
    public String cafeteria_booking() {
        return null;
    }


    private final BookingService bookingService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
}
