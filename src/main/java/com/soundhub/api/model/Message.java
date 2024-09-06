package com.soundhub.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.soundhub.api.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@Builder
public class Message {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;

    @Column(name = "isRead")
    private Boolean isRead;

    @Column(name = "reply_to_message_id")
    private UUID replyToMessageId;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    @JsonIgnore
    private Chat chat;

    @JsonProperty("chat_id")
    public UUID getChatId() {
        return chat.getId();
    }
}
