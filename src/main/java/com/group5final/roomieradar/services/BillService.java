package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Bill;
import com.group5final.roomieradar.entities.BillSplit;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.BillRepository;
import com.group5final.roomieradar.repositories.BillSplitRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillSplitRepository billSplitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Transactional
    public Bill createBill(String name, BigDecimal amount, String description, LocalDateTime dueDate,
                          List<Long> userIds, boolean splitEqually) {
        User currentUser = currentUserService.getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        Household household = currentUser.getHousehold();

        if (household == null) {
            throw new IllegalStateException("User must be in a household to create bills");
        }

        Bill bill = new Bill();
        bill.setName(name);
        bill.setAmount(amount);
        bill.setDescription(description);
        bill.setDueDate(dueDate);
        bill.setHousehold(household);
        bill.setCreatedBy(currentUser);

        bill = billRepository.save(bill);

        List<User> usersToSplit;
        if (splitEqually) {
            usersToSplit = userRepository.findByHouseholdId(household.getId());
        } else {
            usersToSplit = (List<User>) userRepository.findAllById(userIds);
        }

        if (usersToSplit.isEmpty()) {
            throw new IllegalArgumentException("No users found to split the bill");
        }

        BigDecimal splitAmount = amount.divide(
            BigDecimal.valueOf(usersToSplit.size()),
            2,
            RoundingMode.HALF_UP
        );

        for (User user : usersToSplit) {
            BillSplit split = new BillSplit();
            split.setBill(bill);
            split.setUser(user);
            split.setSplitAmount(splitAmount);
            split.setStatus(com.group5final.roomieradar.enums.SplitStatus.UNPAID);
            billSplitRepository.save(split);
        }

        return bill;
    }

    public List<Bill> getBillsByUser(Long userId) {
        return billRepository.findBillsByUserId(userId);
    }

    public List<BillSplit> getBillSplitsByBill(Long billId) {
        return billSplitRepository.findByBillId(billId);
    }

    @Transactional
    public void settleSplit(Long splitId, Long userId) {
        BillSplit split = billSplitRepository.findById(splitId)
            .orElseThrow(() -> new IllegalArgumentException("Bill split not found"));
        
        if (!split.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only settle your own bill splits");
        }

        if (split.getStatus() != com.group5final.roomieradar.enums.SplitStatus.UNPAID) {
            throw new IllegalStateException("Bill split is not in UNPAID status");
        }

        split.setStatus(com.group5final.roomieradar.enums.SplitStatus.PENDING_APPROVAL);
        billSplitRepository.save(split);
    }

    @Transactional
    public void approveSplit(Long splitId, Long creatorId) {
        BillSplit split = billSplitRepository.findById(splitId)
            .orElseThrow(() -> new IllegalArgumentException("Bill split not found"));
        
        if (!split.getBill().getCreatedBy().getId().equals(creatorId)) {
            throw new IllegalArgumentException("Only the bill creator can approve settlements");
        }

        if (split.getStatus() != com.group5final.roomieradar.enums.SplitStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Bill split is not pending approval");
        }

        split.setStatus(com.group5final.roomieradar.enums.SplitStatus.PAID);
        billSplitRepository.save(split);
    }

    @Transactional
    public void deleteBill(Long billId) {
        billRepository.deleteById(billId);
    }
}
