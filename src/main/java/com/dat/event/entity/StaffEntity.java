/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Staff Class.
 * <p>
 * </p>
 *
 * @author
 */

@Entity
@Data
@Table(name = "tbl_staff")
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    private String staffNo;
    private String name;
    private String email;
    private Long doorLogId;
    private String mobile;
    private boolean delFlag;
    private LocalDateTime createdAt;
    private String createdBy;
    @Column(name = "admin_flag")
    private Boolean adminFlag;

}