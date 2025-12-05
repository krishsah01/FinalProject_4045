package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends CrudRepository<Bill, Long> {

    @Query("SELECT DISTINCT b FROM Bill b LEFT JOIN b.splits s WHERE s.user.id = :userId OR b.createdBy.id = :userId")
    List<Bill> findBillsByUserId(@Param("userId") Long userId);
}
