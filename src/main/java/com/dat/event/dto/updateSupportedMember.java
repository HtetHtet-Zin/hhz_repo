package com.dat.event.dto;

import lombok.Data;

@Data
public class updateSupportedMember {
    private Long plannerId;
    private Long memberId;
    private String name;
    private String month;
}
