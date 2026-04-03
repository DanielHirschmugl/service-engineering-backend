package jku.se.dto;

import jku.se.entity.MARK;

public class AddEntryRequest {

    private Long userId;
    private Long musicItemId;
    private MARK mark;

    public AddEntryRequest() {
    }

    public AddEntryRequest(Long userId, Long musicItemId, MARK mark) {
        this.userId = userId;
        this.musicItemId = musicItemId;
        this.mark = mark;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMusicItemId() {
        return musicItemId;
    }

    public void setMusicItemId(Long musicItemId) {
        this.musicItemId = musicItemId;
    }

    public MARK getMark() {
        return mark;
    }

    public void setMark(MARK mark) {
        this.mark = mark;
    }
}