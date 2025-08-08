/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.dto.StaffDto;
import com.dat.event.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * StaffController Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping(WebUrl.STAFFS_URL)
    public String findAll() {
        return "staff";
    }

    @PostMapping(WebUrl.STAFFS_URL)
    public ResponseEntity<Page<StaffDto>> findAll(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page){
        return ResponseEntity.ok(keyword.isBlank() ? staffService.findAll(page) : staffService.findAll(keyword, page));
    }

    @PostMapping(WebUrl.STAFF_URL + "/admin-flag")
    public ResponseEntity<Void> updateAdminFlag(@RequestParam("staffNo") String staffNo,
                                                @RequestParam(value = "adminFlag", defaultValue = "false") boolean adminFlag) {

        staffService.updateAdminFlag(staffNo, adminFlag);

        return ResponseEntity.ok().build();

    }
}
