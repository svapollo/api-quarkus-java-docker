package io.github.svapollo.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="post_text")
    private String text;

    @Column(name="post_datetime")
    private LocalDateTime postDateTime;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @PrePersist
    public void generatePostDatetime(){
        setPostDateTime(LocalDateTime.now());
    }
}
