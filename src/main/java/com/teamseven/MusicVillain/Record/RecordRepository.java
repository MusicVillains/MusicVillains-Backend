package com.teamseven.MusicVillain.Record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {
    List<Record> findAll();
    Record findByRecordId(String recordId);
    void deleteByRecordId(String recordId);
}
