package jku.se.controller;

import jku.se.dto.CreateMusicItemRequest;
import jku.se.dto.UpdateMusicItemRequest;
import jku.se.entity.MusicItem;
import jku.se.repository.EntryRepository;
import jku.se.repository.MusicItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/music-items")
public class MusicItemController {

    private final MusicItemRepository musicItemRepository;
    private final EntryRepository entryRepository;

    public MusicItemController(MusicItemRepository musicItemRepository, EntryRepository entryRepository) {
        this.musicItemRepository = musicItemRepository;
        this.entryRepository = entryRepository;
    }

    @GetMapping
    public List<MusicItem> getAllMusicItems() {
        return musicItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public MusicItem getMusicItemById(@PathVariable Long id) {
        return musicItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found"));
    }

    @PostMapping
    public ResponseEntity<MusicItem> createMusicItem(@RequestBody CreateMusicItemRequest request) {
        MusicItem musicItem = new MusicItem(
                request.getTitle(),
                request.getArtist(),
                request.getType()
        );

        MusicItem saved = musicItemRepository.save(musicItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public MusicItem updateMusicItem(@PathVariable Long id, @RequestBody UpdateMusicItemRequest request) {
        MusicItem musicItem = musicItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found"));

        musicItem.setTitle(request.getTitle());
        musicItem.setArtist(request.getArtist());
        musicItem.setType(request.getType());

        return musicItemRepository.save(musicItem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteMusicItem(@PathVariable Long id) {
        if (!musicItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found");
        }

        entryRepository.deleteByMusicItemId(id);
        musicItemRepository.deleteById(id);
    }
}