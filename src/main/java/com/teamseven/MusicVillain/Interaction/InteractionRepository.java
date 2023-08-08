package com.teamseven.MusicVillain.Interaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, String> {
}
