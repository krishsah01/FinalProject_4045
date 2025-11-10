package com.group5final.roomieradar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event", schema = "roomieRadarData")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "householdId", nullable = false)
    private Household household;

    @Column(name = "description")
    private String description;

    @Column(name="eventtime", nullable = false)
    private Instant eventTime;

}