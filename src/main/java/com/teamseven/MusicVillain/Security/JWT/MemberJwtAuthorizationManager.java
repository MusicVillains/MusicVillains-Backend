package com.teamseven.MusicVillain.Security.JWT;

import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class MemberJwtAuthorizationManager extends JwtAuthorizationManager {
    private MemberRepository memberRepository;
    @Autowired
    public MemberJwtAuthorizationManager(MemberRepository memberRepository, FeedRepository feedRepository) {
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
