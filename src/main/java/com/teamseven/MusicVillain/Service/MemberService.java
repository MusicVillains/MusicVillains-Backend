package com.teamseven.MusicVillain.Service;

import com.teamseven.MusicVillain.Entity.Member;
import com.teamseven.MusicVillain.DTO.MemberRequestDto;
import com.teamseven.MusicVillain.Repository.MemberRepository;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.UUID.*;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers(){
        return memberRepository.findAll();
    }

    public Status insertMember(MemberRequestDto memberRequestDto){
        // 이미 존재하는 멤버인지 확인
        if(memberRepository.findByUserId(memberRequestDto.getUserId()) != null){
            // 이미 존재하는 경우, status code 409 리턴
            System.out.println("이미 존재하는 유저입니다.");
            return Status.CONFLICT;
        }

        // memberRequestDto 필드 값중 하나라도 null인 것이 있는지 확인
        if(memberRequestDto.hasNullField()){
            System.out.println("필드 값중 하나라도 null인 것이 있습니다.");
            return Status.BAD_REQUEST;
        }

        // 새로운 멤버 생성
        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString().replace("-", ""))
                .userId(memberRequestDto.getUserId())
                .userInfo(memberRequestDto.getUserInfo())
                .name(memberRequestDto.getName())
                .email(memberRequestDto.getEmail())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
        System.out.println(member);

        memberRepository.save(member);
        return Status.OK;
    }
}
