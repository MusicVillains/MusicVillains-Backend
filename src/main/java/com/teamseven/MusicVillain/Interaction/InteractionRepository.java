package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, String> {
    List<Interaction> findAll();
    Interaction findByInteractionMemberAndInteractionFeed(Member member, Feed feed);

    Interaction findByInteractionId(String interactionId);

    int countByInteractionFeedFeedId(String feedId);

    void deleteByInteractionFeed(Feed interactionFeed);
    void deleteByInteractionFeedFeedId(String feedId);
    void deleteByInteractionMemberMemberId(String memberId);
    void deleteByInteractionId(String interactionId);
}
