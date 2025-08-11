package com.dat.event.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_event_planner")
public class EventPlannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;
    private String supportedMonth;
    private Boolean supportedMemberFlg;
    private Boolean delFlg;
}
