/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.constant;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * WebUrl Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

@ControllerAdvice
public final class WebUrl {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private WebUrl() {
    }

    public static final String STAFF_URL = "/staff";
    public static final String STAFF_BIRTHDAY_URL = "/staff-birthday";
    public static final String STAFFS_URL = "/staffs";

    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String EVENT_URL = "/event";
    public static final String EVENT_CREATE_URL = "/event-create";
    public static final String EVENT_DELETE_URL = "/event-delete";
    public static final String EVENT_STAFF_DOWNLOAD_URL = EVENT_URL + "/download";
    public static final String EVENT_CHECK_URL = "/event-check";
    public static final String EVENT_EDIT_URL = "/event-edit";
    public static final String EVENTS_URL = "/participant-list";
    public static final String EVENT_REGISTRATION_URL = "/event-registration";
    public static final String EVENT_REGISTRATION_POST_URL = "/register-event-schedule";

    public static final String GALLERY_URL = "/gallery";
    public static final String API_URL = "/api";

    public static final String EVENT_API_URL = API_URL + EVENT_URL;

    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("contextPath")
    public String contextPath() {
        return this.contextPath;
    }

}
