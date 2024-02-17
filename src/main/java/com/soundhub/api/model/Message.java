package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne
    private User sender;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "isRead")
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
