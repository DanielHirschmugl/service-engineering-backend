package jku.se.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "music_item")
public class MusicItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MUSICITEMTYPE type;

    public MusicItem() {
    }

    public MusicItem(String title, String artist, MUSICITEMTYPE type) {
        this.title = title;
        this.artist = artist;
        this.type = type;
    }

    public Long getId() {
        return id;
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