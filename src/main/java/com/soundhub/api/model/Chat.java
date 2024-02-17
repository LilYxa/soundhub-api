package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> participants;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
    private List<Message> messages;

    @OneToOne
    private Message lastMessage;
}
