package jku.se.controller;

import jku.se.dto.*;
import jku.se.entity.*;
import jku.se.repository.*;
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
    private final UserRepository userRepository;

    public MusicItemController(MusicItemRepository musicItemRepository,
                               EntryRepository entryRepository,
                               UserRepository userRepository) {
        this.musicItemRepository = musicItemRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
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
    public ResponseEntity<MusicItem> createMusicItem(@RequestParam Long requestUserId,
                                                     @RequestBody CreateMusicItemRequest request) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (requestUser.getRole() != ROLE.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create music items");
        }

        MusicItem musicItem = new MusicItem(
                request.getTitle(),
                request.getArtist(),
                request.getType()
        );

        MusicItem saved = musicItemRepository.save(musicItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public MusicItem updateMusicItem(@PathVariable Long id,
                                     @RequestParam Long requestUserId,
                                     @RequestBody UpdateMusicItemRequest request) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (requestUser.getRole() != ROLE.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update music items");
        }

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
    public void deleteMusicItem(@PathVariable Long id,
                                @RequestParam Long requestUserId) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (requestUser.getRole() != ROLE.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete music items");
        }

        if (!musicItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found");
        }

        entryRepository.deleteByMusicItemId(id);
        musicItemRepository.deleteById(id);
    }
}