package com.teamseven.MusicVillain.Service;

import com.teamseven.MusicVillain.Entity.Member;
import com.teamseven.MusicVillain.Repository.MemberRepository;
import com.teamseven.MusicVillain.Entity.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login 요청이 들어오면, 스프링 시큐리티에서 자동으로 UserDetailsService 타입으로 IoC에 등록되어 있는 되어 있는 UserDetailsService의 loadUserByUsername 함수가 실행된다.
@Service

public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 반드시 Request body 내에 Key가 "username" 이어야 함.
        System.out.println("[DEBUG] userId: " + username);

        // 멤버 존재 여부 확인
        Member memberEntity = memberRepository.findByUserId(username);
        if(memberEntity != null){
            System.out.println("[DEBUG] login member.role: " + memberEntity.getRole());
            return new UserDetailsImpl(memberEntity); // Authentication 객체 내부로 리턴된다.
        }
        return null;
    }
}