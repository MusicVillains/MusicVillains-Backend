package com.teamseven.MusicVillain.Security;


import com.teamseven.MusicVillain.Member.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class UserDetailsImpl implements UserDetails, OAuth2User {
    // 현재 프로젝트에 정의한 유저에 대응되는 엔티티인 Member 객체를 가져옴
    private Member member;
    private Map<String, Object> attributes;

    public UserDetailsImpl(Member member) {
        super();
        this.member = member;
    }
    public UserDetailsImpl(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorityList = new ArrayList<>();
        member.getRoleList().forEach(r -> {
            authorityList.add(()->"ROLE_" + r);
        });

        return authorityList;
    }

    @Override
    public String getPassword() {
        // 사용자 패스워드
        return member.getUserInfo();
    }

    @Override
    public String getUsername() {
        // 사용자 아이디
        return member.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부
        return true; // 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠김 여부
        return true; // 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 계정 패스워드 만료 여부
        return true; // 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        // 계정 사용 가능 여부
        return true; // 사용 가능
    }

    @Override
    public String getName() {
        return null;
    }
}