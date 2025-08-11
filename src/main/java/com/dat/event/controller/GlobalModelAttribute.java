/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import org.springframework.beans.factory.annotation.Value;
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
    public Class<WebUrl> WebUrl(){
        return WebUrl.class;
    }

}
