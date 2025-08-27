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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

}
