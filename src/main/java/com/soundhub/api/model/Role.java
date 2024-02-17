package com.soundhub.api.model;

import com.soundhub.api.enums.RoleName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @NaturalId
    @Column(name = "name")
    private RoleName name;
}
