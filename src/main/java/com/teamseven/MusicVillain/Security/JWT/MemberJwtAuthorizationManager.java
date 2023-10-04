package com.teamseven.MusicVillain.Security.JWT;

import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
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
        log.debug("MemberJwtAuthorizationManager.authorize(String jwtToken, String memberId) called\n"
                        + "\t* params\n"
                        + "\t\t- jwtToken:\n"
                        + "\t\t\t{}\n"
                        + "\t\t- memberId:\n"
                        + "\t\t\t{}\n", jwtToken, memberId);


        if (this.authorizeMode == false) return AuthorizationResult.success(memberId, null);

        ServiceResult verifyResult = JwtManager.verifyAccessToken(jwtToken);
        log.debug("verifyResult : {}", verifyResult);

        if(verifyResult.isFailed())
        {
            log.warn("verifyResult is failed - {}" , verifyResult.getMessage());
            return AuthorizationResult.fail(verifyResult.getMessage());}

        String authorizedMemberId = (String) verifyResult.getData();
        log.debug("authorizedMemberId : {}", authorizedMemberId);

        if(!authorizedMemberId.equals(memberId)) {
            log.warn("Authorization failed, memberId does not match authorized memberId.\n" +
                    "\t\t- memberId: {}\n" +
                    "\t\t- Authorized memberId: {}", memberId, authorizedMemberId);

            return AuthorizationResult.fail("memberId does not match authorized memberId.");
        }
        Member member = memberRepository.findByMemberId(memberId);

        log.debug("Authorization succeeded, member : {}", member);
        return AuthorizationResult.success("Authorization succeeded", member);
    }

    @Override
    public AuthorizationResult authenticate(String jwtToken, String entityId) {
        return null;
    }
}
