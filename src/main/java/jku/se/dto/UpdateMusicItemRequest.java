package jku.se.dto;

import jku.se.entity.MUSICITEMTYPE;

public class UpdateMusicItemRequest {

    private String title;
    private String artist;
    private MUSICITEMTYPE type;

    public UpdateMusicItemRequest() {
    }

    public UpdateMusicItemRequest(String title, String artist, MUSICITEMTYPE type) {
        this.title = title;
        this.artist = artist;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public MUSICITEMTYPE getType() {
        return type;
    }

    public void setType(MUSICITEMTYPE type) {
        this.type = type;
    }
}