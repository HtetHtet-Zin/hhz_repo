/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.dto;

import com.dat.event.entity.EventScheduleEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * BookingDto Class.
 * <p>
 * </p>
 *
 * @author
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;
    private String eventName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookedDate;
    private String status;
    private String name;
    private String team;
    private String department;
    private LocalDate date;
    private String startTime;
    private String endTime;

    private EventScheduleDto schedule;
    private String bookedBy;
    private String reason;
    private String confirmedBy;
    private LocalDateTime confirmedDate;
    private int attendees;
    private boolean delFlag;
    private LocalDateTime updateAt;
    private String updatedBy;


}
