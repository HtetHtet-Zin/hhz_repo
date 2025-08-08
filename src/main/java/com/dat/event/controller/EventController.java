/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.dto.EventStaffDto;
import com.dat.event.service.EventRegistrationService;
import com.dat.event.service.EventService;
import com.dat.event.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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

    @PostMapping(WebUrl.EVENT_URL + "/create")
    public ResponseEntity<?> createEvent(
            @RequestParam String eventName,
            @RequestParam String inChargePerson,
            @RequestParam MultipartFile file,
            @RequestParam String description
    ) {
        // handle text params
        System.out.println("dex" + description);
        System.out.println("Event Name: " + eventName);
        System.out.println("Incharge: " + inChargePerson);

        // handle file
        if (!file.isEmpty()) {
            System.out.println("Uploaded file: " + file.getOriginalFilename());
        }

        return ResponseEntity.ok("Event created successfully!");
    }


    @GetMapping(WebUrl.EVENT_URL)
    public String view(HttpSession session){
        if (session != null && session.getAttribute("staffNo") != null) {
            return "event";
        }
        return WebUrl.LOGIN_URL;
    }
    @GetMapping("/event-list")
    public String eventList() {
        return "event-list";
    }

    @PostMapping("/event-list")
    public ResponseEntity<Page<EventStaffDto>> eventList(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(keyword.isBlank() ? eventRegistrationService.fetchEventStaffList(null, page) : eventRegistrationService.fetchEventStaffList(keyword, page));
    }


}
