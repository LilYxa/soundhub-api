package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats")
@Builder
public class Chat {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<User> participants;

    @ManyToOne(fetch = FetchType.EAGER)
    private User createdBy;

    @Column(name = "is_group")
    private Boolean isGroup;

    @Column(name = "chat_image")
    private String chatImage;

    @Column(name = "chat_name")
    private String chatName;

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", isGroup=" + isGroup +
                ", chatImage='" + chatImage + '\'' +
                ", chatName='" + chatName + '\'' +
                '}';
    }
}
