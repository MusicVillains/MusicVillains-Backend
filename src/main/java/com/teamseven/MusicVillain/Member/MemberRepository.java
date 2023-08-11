package com.teamseven.MusicVillain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findAll();
    Member findByUserId(String userId);
    Member findByName(String name);
    Member findByEmail(String email);
    Member findByMemberId(String MemberId);
    void deleteByMemberId(String memberId);
}
