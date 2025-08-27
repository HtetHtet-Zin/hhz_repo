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

    private Long staffId;
    private String staffPhoto;
    private String staffNo;
    private String name;
    private String email;
    private String mobile;
    private String team;
    private String department;
    private LocalDate dob;
    private Boolean adminFlag;
    private Boolean approverFlag;

}
