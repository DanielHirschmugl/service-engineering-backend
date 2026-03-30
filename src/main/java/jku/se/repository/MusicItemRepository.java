package jku.se.repository;

import jku.se.entity.MusicItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicItemRepository extends JpaRepository<MusicItem, Long> {
}