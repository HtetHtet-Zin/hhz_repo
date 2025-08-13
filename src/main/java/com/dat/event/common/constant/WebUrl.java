/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.constant;

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

    private WebUrl() {
    }

    public static final String ERROR_URL = "/404";
    public static final String STAFF_URL = "/staff";

    public static final String STAFF_BIRTHDAY_URL = "/staff-birthday";

    public static final String STAFFS_URL = "/staffs";

    public static final String LOGIN_URL = "/login";

    public static final String LOGOUT_URL = "/logout";

    public static final String EVENT_URL = "/event";
    
    public static final String EVENT_CREATE_URL = "/event-create";

    public static final String EVENTS_URL = "/event-list";
    public static final String EVENT_REGISTRATION_URL = "/event-registration";

    public static final String GALLERY_URL = "/gallery";

    public static final String API_URL = "/api";

    public static final String EVENT_API_URL = API_URL + EVENT_URL;


}
