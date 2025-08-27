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
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public String findAll(HttpSession session) throws AccessDeniedException {
        if (session != null && session.getAttribute("staffNo") != null) {
            Object isAdmin = session.getAttribute("adminFlag");
            if (!Boolean.TRUE.equals(isAdmin)) {
                throw new AccessDeniedException("No Permission To - " + session.getAttribute("staffNo"));
            }
            return "staff";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @PostMapping(WebUrl.STAFFS_URL)
    public ResponseEntity<Page<StaffDto>> findAll(@RequestParam(required = false, defaultValue = "") final String keyword, @RequestParam(required = false, defaultValue = "0") final int page) {
        return ResponseEntity.ok(keyword.isBlank() ? staffService.findAll(page) : staffService.findAll(keyword, page));
    }

    @PostMapping(WebUrl.STAFF_URL + "/admin-flag")
    public ResponseEntity<Void> updateAdminFlag(@RequestParam("staffNo") String staffNo,
                                                @RequestParam(value = "adminFlag", defaultValue = "false") boolean adminFlag) {

        staffService.updateAdminFlag(staffNo, adminFlag);

        return ResponseEntity.ok().build();

    }

    @PostMapping(WebUrl.STAFF_URL + "/approver-flag")
    public ResponseEntity<Void> updateApproverFlag(@RequestParam("staffNo") String staffNo,
                                                   @RequestParam(value = "approverFlag", defaultValue = "false") boolean approverFlag) {

        staffService.updateApproverFlag(staffNo, approverFlag);

        return ResponseEntity.ok().build();

    }

    @GetMapping(WebUrl.STAFF_BIRTHDAY_URL)
    public String birthdayStaff(HttpSession session, Model model) {
        if (session != null && session.getAttribute("staffNo") != null) {
            var staffNoList = staffService.birthdayStaffList();
            String projectRoot = System.getProperty("user.dir");
            Path imageDir = Paths.get(projectRoot, "photo", "birthdayPhoto");
            staffNoList.forEach(staffDto -> {
                Path photoPath = imageDir.resolve(staffDto.getStaffNo() + ".jpg");
                if (!Files.exists(photoPath)) {
                    staffDto.setStaffPhoto("default");
                }else {
                    staffDto.setStaffPhoto(staffDto.getStaffNo());
                }
            });

            model.addAttribute("birthdayStaffList", staffNoList);
            return "birthday";
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }
}
