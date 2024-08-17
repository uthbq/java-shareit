package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID", nullable = false)
    private User author;
    @Column(name = "CREATED", nullable = false)
    private LocalDateTime created;
}
