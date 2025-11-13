package com.group5final.roomieradar.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "household_members", schema = "roomieRadarData")
@IdClass(HouseholdMember.HouseholdMemberId.class)
public class HouseholdMember {

    @Id
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", insertable = false, updatable = false)
    private Household household;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Composite key class
    @Getter
    @Setter
    public static class HouseholdMemberId implements Serializable {
        private Long householdId;
        private Long userId;

        public HouseholdMemberId() {}

        public HouseholdMemberId(Long householdId, Long userId) {
            this.householdId = householdId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HouseholdMemberId that = (HouseholdMemberId) o;
            return householdId.equals(that.householdId) && userId.equals(that.userId);
        }

        @Override
        public int hashCode() {
            return householdId.hashCode() + userId.hashCode();
        }
    }
}

