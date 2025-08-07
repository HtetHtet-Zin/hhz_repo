/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * StaffDto Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */
@Data
public class StaffDto {
    private String staffNo;
    private String name;
    private String email;
    private String mobile;
    private LocalDate dob;
    private Boolean adminFlag;

}
