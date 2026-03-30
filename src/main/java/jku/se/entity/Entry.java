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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;
}
