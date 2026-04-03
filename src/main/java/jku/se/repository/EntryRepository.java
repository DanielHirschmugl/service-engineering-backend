package jku.se.repository;

import jku.se.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByUserId(Long userId);

    Optional<Entry> findByUserIdAndMusicItemId(Long userId, Long musicItemId);

    void deleteByMusicItemId(Long musicItemId);
}