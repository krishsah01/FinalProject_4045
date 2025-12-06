package com.group5final.roomieradar.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "calendarItem", schema = "roomieRadarData")
public class CalendarItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "dateStart", nullable = false)
    private LocalDateTime dateStart;

    @Column(name = "dateEnd")
    private LocalDateTime dateEnd;

    @Column(name = "repeatDuration")
    private Integer repeatDuration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "householdId", nullable = false)
    private Household household;

}