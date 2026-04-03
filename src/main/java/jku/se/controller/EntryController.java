package jku.se.controller;

import jku.se.dto.AddEntryRequest;
import jku.se.dto.UpdateMarkRequest;
import jku.se.entity.Entry;
import jku.se.entity.MARK;
import jku.se.entity.MusicItem;
import jku.se.entity.User;
import jku.se.repository.EntryRepository;
import jku.se.repository.MusicItemRepository;
import jku.se.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EntryController {

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final MusicItemRepository musicItemRepository;

    public EntryController(EntryRepository entryRepository,
                           UserRepository userRepository,
                           MusicItemRepository musicItemRepository) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.musicItemRepository = musicItemRepository;
    }

    @PostMapping("/entries")
    public ResponseEntity<Map<String, Object>> addEntry(@RequestBody AddEntryRequest request) {
        if (entryRepository.findByUserIdAndMusicItemId(request.getUserId(), request.getMusicItemId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Entry already exists");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        MusicItem musicItem = musicItemRepository.findById(request.getMusicItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found"));

        MARK mark = request.getMark() != null ? request.getMark() : MARK.LIKE;

        Entry entry = new Entry(user, musicItem, mark);
        Entry saved = entryRepository.save(entry);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/entries")
    public List<Map<String, Object>> getAllEntries() {
        return entryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/users/{userId}/entries")
    public List<Map<String, Object>> getEntriesByUser(@PathVariable Long userId) {
        return entryRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/entries/{entryId}/mark")
    public Map<String, Object> updateMark(@PathVariable Long entryId, @RequestBody UpdateMarkRequest request) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        entry.setMark(request.getMark());
        Entry saved = entryRepository.save(entry);

        return toResponse(saved);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long entryId) {
        if (!entryRepository.existsById(entryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        entryRepository.deleteById(entryId);
    }

    private Map<String, Object> toResponse(Entry entry) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("entryId", entry.getId());
        response.put("userId", entry.getUser().getId());
        response.put("userName", entry.getUser().getName());
        response.put("musicItemId", entry.getMusicItem().getId());
        response.put("title", entry.getMusicItem().getTitle());
        response.put("artist", entry.getMusicItem().getArtist());
        response.put("type", entry.getMusicItem().getType());
        response.put("mark", entry.getMark());
        return response;
    }
}