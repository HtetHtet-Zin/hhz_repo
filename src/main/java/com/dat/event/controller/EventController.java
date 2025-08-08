/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.service.EventRegistrationService;
import com.dat.event.service.EventService;
import com.dat.event.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * EventController Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class EventController {

    private final EventService eventService;
    private final EventRegistrationService eventRegistrationService;
    private final StaffService staffService;

    @GetMapping(WebUrl.EVENT_URL + "/create")
    public ModelAndView showCreateEventPage() {
        var staffDtoList=staffService.findAll();
        log.info("staff-list {}",staffDtoList);
        return new ModelAndView("event-create-page", "staffs", staffDtoList);
    }

    @GetMapping(WebUrl.EVENT_URL)
    public String view(HttpSession session){
        if (session != null && session.getAttribute("staffNo") != null) {
            return "event";
        }
        return WebUrl.LOGIN_URL;
    }
    @GetMapping("/event-list")
    public ModelAndView eventList() {
        return new ModelAndView("event-list", "staffs", eventRegistrationService.fetchEventStaffList());
    }


}
