package com.teamseven.MusicVillain.Interaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, String> {
    List<Interaction> findAll();
}
