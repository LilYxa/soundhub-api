package com.soundhub.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soundhub.api.Constants;
import com.soundhub.api.enums.InviteStatus;
import io.netty.util.Constant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Invite {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;

    @Column(name = "created_datetime")
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT)
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private InviteStatus status;
}
