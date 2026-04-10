package jku.se.controller;

import jku.se.dto.*;
import jku.se.entity.*;
import jku.se.repository.*;
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
    public ResponseEntity<Map<String, Object>> addEntry(@RequestParam Long requestUserId,
                                                        @RequestBody AddEntryRequest request) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (!requestUser.getId().equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only add music to your own collection");
        }

        if (entryRepository.findByUserIdAndMusicItemId(request.getUserId(), request.getMusicItemId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Entry already exists");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found"));

        MusicItem musicItem = musicItemRepository.findById(request.getMusicItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Music item not found"));

        MARK mark = request.getMark() != null ? request.getMark() : MARK.LIKE;

        Entry entry = new Entry(user, musicItem, mark);
        Entry saved = entryRepository.save(entry);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/entries")
    public List<Map<String, Object>> getAllEntries(@RequestParam Long requestUserId) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (requestUser.getRole() != ROLE.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all entries");
        }

        return entryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/users/{userId}/entries")
    public List<Map<String, Object>> getEntriesByUser(@PathVariable Long userId,
                                                      @RequestParam Long requestUserId) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        if (requestUser.getRole() != ROLE.ADMIN && !requestUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own collection");
        }

        return entryRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/entries/{entryId}/mark")
    public Map<String, Object> updateMark(@PathVariable Long entryId,
                                          @RequestParam Long requestUserId,
                                          @RequestBody UpdateMarkRequest request) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (requestUser.getRole() != ROLE.ADMIN && !requestUser.getId().equals(entry.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own entries");
        }

        entry.setMark(request.getMark());
        Entry saved = entryRepository.save(entry);

        return toResponse(saved);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long entryId,
                            @RequestParam Long requestUserId) {
        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request user not found"));

        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (requestUser.getRole() != ROLE.ADMIN && !requestUser.getId().equals(entry.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own entries");
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