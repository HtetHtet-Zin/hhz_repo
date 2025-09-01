/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.CommonUtility;
import com.dat.event.common.constant.WebUrl;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GlobalModelAttribute Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute("WebUrl")
    public Class<WebUrl> WebUrl() {
        return WebUrl.class;
    }

    @ModelAttribute("IsAdmin")
    public boolean isAdmin(HttpSession httpSession) {
        Object isAdmin = httpSession.getAttribute("adminFlag");
        if (isAdmin != null) {
            if (isAdmin instanceof Boolean b) {
                return b;
            }
        }
        return false;
    }

    @ModelAttribute("IsApprover")
    public boolean isApproval(HttpSession httpSession) {
        Object isApproval = httpSession.getAttribute("approval");
        if (isApproval != null) {
            if (isApproval instanceof Boolean b) {
                return b;
            }
        }
        return false;
    }

    @ModelAttribute("StaffName")
    public String currentStaffName(HttpSession httpSession) {
        Object staffName = httpSession.getAttribute("name");
        if (staffName != null) {
            return staffName.toString();
        }
        return "";
    }

    @ModelAttribute("StaffNo")
    public String currentStaffNo(HttpSession httpSession) {
        Object staffNo = httpSession.getAttribute("staffNo");
        if (staffNo != null) {
            return staffNo.toString();
        }
        return "";
    }

    @ModelAttribute("Team")
    public String currentSTeam(HttpSession httpSession) {
        Object team = httpSession.getAttribute("team");
        if (team != null) {
            return team.toString();
        }
        return "";
    }

    @ModelAttribute("Department")
    public String currentDepartment(HttpSession httpSession) {
        Object department = httpSession.getAttribute("department");
        if (department != null) {
            return department.toString();
        }
        return "";
    }

    @ModelAttribute("CommonUtility")
    public Class<CommonUtility> commonUtilityClass() {
        return CommonUtility.class;
    }
}
