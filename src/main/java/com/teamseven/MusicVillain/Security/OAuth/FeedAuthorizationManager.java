package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.JwtManager;
import com.teamseven.MusicVillain.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class FeedAuthorizationManager extends AuthorizationManager{

    private FeedRepository feedRepository;

    @Autowired
    public FeedAuthorizationManager(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public AuthorizationResult authorize(String jwtToken, String feedId) {
        if (this.authorizeMode == false)
            return AuthorizationResult.success(feedId, null);

        Feed feed = feedRepository.findByFeedId(feedId);

        if (feed == null)
            return AuthorizationResult.fail("feedId does not exist.");

        // check jwtToken is valid
        ServiceResult verifyResult = JwtManager.verifyToken(jwtToken);
        if(verifyResult.isFailed())
            return AuthorizationResult.fail();

        // check this member is feed's owner
        String tmpMemberId = verifyResult.getData().toString();
        String feedOwnerMemberId = feed.getOwner().getMemberId();

        if(!tmpMemberId.equals(feedOwnerMemberId))
            return AuthorizationResult.fail("Feed owner's memberId does not match authorized memberId.");

        return AuthorizationResult.success("Authorization succeeded", null);
    }

    @Override
    public AuthorizationResult authenticate(String jwtToken, String entityId) {
        return null;
    }
}
