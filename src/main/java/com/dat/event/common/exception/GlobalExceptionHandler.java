/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.exception;

import com.dat.event.common.constant.WebUrl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler Class.
 * <p>
 * </p>
 *
 * @author Zwe Naing Oo
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, HttpSession session) {
        if (session != null && session.getAttribute("staffNo") != null) {
            session.invalidate();
        }
        model.addAttribute("LOGIN_URL", WebUrl.LOGIN_URL);
        //ex.printStackTrace();
        return "404";
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
