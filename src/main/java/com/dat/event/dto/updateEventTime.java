package com.dat.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class updateEventTime {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime startDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime endDateTime;
   private Long eventTimeId;
}
