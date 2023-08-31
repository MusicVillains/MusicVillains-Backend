package com.teamseven.MusicVillain.Security.JWT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {
    List<JwtToken> findAll();
    JwtToken findByOwnerIdAndType(String ownerId, String type);
    JwtToken findByValue(String value);
    JwtToken findByValueAndType(String value, String type);
    void deleteAllByOwnerId(String ownerId);
    void deleteAllByOwnerIdAndType(String ownerId, String type);

}
