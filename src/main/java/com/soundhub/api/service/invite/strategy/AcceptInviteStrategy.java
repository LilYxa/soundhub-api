package com.soundhub.api.service.invite.strategy;

import com.soundhub.api.Constants;
import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Implementation of the {@link InviteStrategy} interface for accepting an invitation.
 * This strategy validates the recipient, updates the invitation's status to {@code ACCEPTED},
 * adds the sender as a friend using {@link UserService}, and removes the invitation from the repository.
 */
@Service
@Slf4j
public class AcceptInviteStrategy implements InviteStrategy {

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserService userService;

    /**
     * Accepts an invitation.
     *
     * @param inviteRecipient The recipient of the invitation.
     * @param inviteId The unique identifier of the invitation to accept.
     * @return The updated {@link Invite} object with its status set to {@code ACCEPTED}.
     * @throws ResourceNotFoundException If the invitation is not found in the repository.
     * @throws ApiException If the provided recipient does not match the invitation's recipient.
     */
    @Override
    public Invite execute(User inviteRecipient, UUID inviteId) {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME, Constants.ID_FIELD, inviteId));

        if (!inviteRecipient.equals(invite.getRecipient())) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        userService.addFriend(invite.getSender().getId());
        inviteRepository.delete(invite);

        log.info("acceptInvite[2]: invite: {} was accepted", invite);
        return invite;
    }
}
