package com.teamseven.MusicVillain.Entity;


import com.teamseven.MusicVillain.Entity.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserDetailsImpl implements UserDetails {
    // 현재 프로젝트에 정의한 유저에 대응되는 엔티티인 Member 객체를 가져옴
    private Member member;

    public UserDetailsImpl(Member member) {
        this.member = member;
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
}