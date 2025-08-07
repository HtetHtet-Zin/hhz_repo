/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
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
    private LocalDate dob;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "staff")
    private List<EventRegistrationEntity> eventRegistrationEntityList = new ArrayList<>();
}