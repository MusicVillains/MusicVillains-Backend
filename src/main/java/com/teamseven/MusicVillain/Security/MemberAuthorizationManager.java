package com.teamseven.MusicVillain.Security;

import com.teamseven.MusicVillain.ENV;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Security.OAuth.AuthorizationManager;
import com.teamseven.MusicVillain.Security.OAuth.AuthorizationResult;
import com.teamseven.MusicVillain.ServiceResult;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class MemberAuthorizationManager extends AuthorizationManager {
    private MemberRepository memberRepository;
    @Autowired
    public MemberAuthorizationManager(MemberRepository memberRepository, FeedRepository feedRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public AuthorizationResult authorize(String jwtToken, String memberId) {
        if (this.authorizeMode == false) return AuthorizationResult.success(memberId, null);

        ServiceResult verifyResult = JwtManager.verifyToken(jwtToken);

        if(verifyResult.isFailed())
            return AuthorizationResult.fail(verifyResult.getMessage());

        String authorizedMemberId = (String) verifyResult.getData();

        if(!authorizedMemberId.equals(memberId))
            return AuthorizationResult.fail("memberId does not match authorized memberId.");

        Member member = memberRepository.findByMemberId(memberId);

        return AuthorizationResult.success("Authorization succeeded", member);
    }

    @Override
    public AuthorizationResult authenticate(String jwtToken, String entityId) {
        return null;
    }
}
