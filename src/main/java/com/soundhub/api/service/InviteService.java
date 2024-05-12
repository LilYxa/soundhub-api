package com.soundhub.api.service;

import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface InviteService {
    Invite createInvite(User sender, User recipient);

    Invite acceptInvite(User inviteRecipient, UUID inviteId) throws IOException;

    Invite rejectInvite(User inviteRecipient, UUID inviteId);

    List<Invite> getAllInvites(User user);

    Invite deleteInvite(User user, UUID inviteId);

    Invite getInviteBySenderAndRecipient(UUID senderId, UUID recipientId);
}
