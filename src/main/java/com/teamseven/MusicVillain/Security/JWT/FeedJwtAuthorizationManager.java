package com.teamseven.MusicVillain.Security.JWT;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedJwtAuthorizationManager extends JwtAuthorizationManager {

    private FeedRepository feedRepository;

    @Autowired
    public FeedJwtAuthorizationManager(FeedRepository feedRepository) {
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
        if (verifyResult.isFailed())
            return AuthorizationResult.fail();



        // check this member is feed's owner
        String tmpMemberId = verifyResult.getData().toString();
        String feedOwnerMemberId = feed.getOwner().getMemberId();

        if (tmpMemberId == null || feedOwnerMemberId == null)
            return AuthorizationResult.fail("Member Not Found");


        if(!tmpMemberId.equals(feedOwnerMemberId))
            return AuthorizationResult.fail("Feed owner's memberId does not match authorized memberId.");

        return AuthorizationResult.success("Authorization succeeded", null);
    }

    @Override
    public AuthorizationResult authenticate(String jwtToken, String entityId) {
        return null;
    }
}
