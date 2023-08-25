package com.teamseven.MusicVillain.Feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, String> {
    List<Feed> findAll();
    List<Feed> findAllByOrderByCreatedAtDesc();
    Feed findByFeedId(String feedId);
    List<Feed> findAllByFeedType(String feedType);
    List<Feed> findAllByOwnerMemberId(String ownerId);

    void deleteByFeedId(String feedId);
    void deleteByOwnerMemberId(String ownerId);
}
