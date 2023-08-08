package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<Member> getAllMembers(){
        return memberRepository.findAll();
    }

    public Status insertMember(MemberCreationRequestBody memberCreationRequestBody){
        // 이미 존재하는 멤버인지 확인
        if(memberRepository.findByUserId(memberCreationRequestBody.getUserId()) != null){
            // 이미 존재하는 경우, status code 409 리턴
            System.out.println("이미 존재하는 유저입니다.");
            return Status.CONFLICT;
        }

        // memberRequestDto 필드 값중 하나라도 null인 것이 있는지 확인
        if(memberCreationRequestBody.hasNullField()){
            System.out.println("필드 값중 하나라도 null인 것이 있습니다.");
            return Status.BAD_REQUEST;
        }

        // 사용자 아이디에 특수문자가 들어가거나 숫자로 시작하는지 검사
        if (!isValidUserId(memberCreationRequestBody.getUserId())) {
            System.out.println("유효하지 않은 사용자 아이디입니다.");
            return Status.BAD_REQUEST;
        }


        // 새로운 멤버 생성
        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString().replace("-", ""))
                .userId(memberCreationRequestBody.getUserId())
                .userInfo(bCryptPasswordEncoder.encode(memberCreationRequestBody.getUserInfo()))
                .name(memberCreationRequestBody.getName())
                .email(memberCreationRequestBody.getEmail())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
        System.out.println(member);

        memberRepository.save(member);
        return Status.OK;

    }

    // 사용자 아이디 유효성 검사 메서드
    private boolean isValidUserId(String userId) {
        // 특수문자 또는 숫자로 시작하는지 검사하는 정규표현식
        String pattern = "^[a-zA-Z][a-zA-Z0-9]*$";
        return userId.matches(pattern);
    }
}
