package jku.se.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "collection_entry",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "music_item_id"})
        }
)
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "music_item_id", nullable = false)
    private MusicItem musicItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MARK mark;

    public Entry() {
    }

    public Entry(User user, MusicItem musicItem, MARK mark) {
        this.user = user;
        this.musicItem = musicItem;
        this.mark = mark;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MusicItem getMusicItem() {
        return musicItem;
    }

    public void setMusicItem(MusicItem musicItem) {
        this.musicItem = musicItem;
    }

    public MARK getMark() {
        return mark;
    }

    public void setMark(MARK mark) {
        this.mark = mark;
    }
}