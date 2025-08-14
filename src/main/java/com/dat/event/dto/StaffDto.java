/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDate dob;
    private Boolean adminFlag;

}
