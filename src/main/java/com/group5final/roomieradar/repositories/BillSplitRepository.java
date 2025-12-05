package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.BillSplit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BillSplitRepository extends CrudRepository<BillSplit, Long> {
    List<BillSplit> findByBillId(Long billId);
    List<BillSplit> findByUserId(Long userId);
}

