package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.HouseholdMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseholdMemberRepository extends CrudRepository<HouseholdMember, HouseholdMember.HouseholdMemberId> {
    List<HouseholdMember> findByHouseholdId(Long householdId);
    List<HouseholdMember> findByUserId(Long userId);
}

