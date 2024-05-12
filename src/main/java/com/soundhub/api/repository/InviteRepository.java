package com.soundhub.api.repository;

import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteRepository extends JpaRepository<Invite, UUID> {
    Boolean existsBySenderAndRecipient(User sender, User recipient);

    List<Invite> findAllByRecipient(User user);

    Optional<Invite> findInviteBySenderAndRecipient(User sender, User recipient);
}
