package com.teamseven.MusicVillain.Feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, String> {
    Feed findByFeedId(String feedId);
}
