package com.teamseven.MusicVillain.Withdrawal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, String> {
    List<Withdrawal> findAll();
    Withdrawal findByWithdrawalId(String memberId);
    void deleteByWithdrawalId(String memberId);
}

