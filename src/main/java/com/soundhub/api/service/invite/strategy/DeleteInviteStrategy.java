package com.soundhub.api.service.invite.strategy;

import com.soundhub.api.Constants;
import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of the {@link InviteStrategy} interface for deleting an invitation.
 * This strategy validates the sender of the invitation, marks the invitation as
 * {@code DELETED_BY_SENDER}, and removes it from the repository.
 */
@Service
@Slf4j
public class DeleteInviteStrategy implements InviteStrategy {

    @Autowired
    private InviteRepository inviteRepository;

    /**
     * Deletes an invitation.
     *
     * @param sender The sender of the invitation.
     * @param inviteId The unique identifier of the invitation to delete.
     * @return The updated {@link Invite} object with its status set to {@code DELETED_BY_SENDER}.
     * @throws ResourceNotFoundException If the invitation is not found in the repository.
     * @throws ApiException If the provided sender does not match the invitation's sender.
     */
    @Override
    public Invite execute(User sender, UUID inviteId) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME, Constants.ID_FIELD, inviteId));

        if (!sender.equals(invite.getSender())) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        invite.setStatus(InviteStatus.DELETED_BY_SENDER);
        inviteRepository.delete(invite);

        log.info("deleteInvite[2]: invite {} was successfully deleted", invite);
        return invite;
    }
}

