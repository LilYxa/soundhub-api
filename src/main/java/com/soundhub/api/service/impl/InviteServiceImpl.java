package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.InviteAlreadySentException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import com.soundhub.api.service.InviteService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class InviteServiceImpl implements InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserService userService;

    @Override
    public Invite createInvite(User sender, User recipient) {
        log.info("createInvite[1]: user {} send invitation to friends for {}", sender, recipient);

        if (sender.getFriends().contains(recipient)) {
            log.error("createInvite[1]: error: {}", Constants.USER_ALREADY_FRIEND);
            throw new ApiException(HttpStatus.BAD_REQUEST, Constants.USER_ALREADY_FRIEND);
        }

        if (sender.equals(recipient)) {
            log.error("createInvite[2]: {}", Constants.USER_CANNOT_ADD_HIMSELF);
            throw new ApiException(HttpStatus.BAD_REQUEST, Constants.USER_CANNOT_ADD_HIMSELF);
        }

        if (inviteRepository.existsBySenderAndRecipient(sender, recipient)) {
            log.error("createInvite[3]: error: {}", Constants.INVITE_ALREADY_SENT_MSG);
            throw new InviteAlreadySentException(Constants.INVITE_ALREADY_SENT_MSG);
        }

        Invite invite = Invite.builder()
                .createdDateTime(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.CONSIDERED)
                .build();
        inviteRepository.save(invite);
        log.info("createInvite[2]: invite was successful saved, id: {}", invite.getId());
        return invite;
    }

    @Override
    public Invite acceptInvite(User inviteRecipient, UUID inviteId) throws IOException {
        log.info("acceptInvite[1]: accept friend invitation with id: {}", inviteId);

        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME, Constants.ID_FIELD, inviteId));

        if (!inviteRecipient.equals(invite.getRecipient())) {
            log.error("acceptInvite[1]: You don't have permissions for this action!");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        userService.addFriend(invite.getSender().getId());
        inviteRepository.delete(invite);
        log.info("acceptInvite[2]: invite: {} was accepted", invite);
        return invite;
    }

    @Override
    public Invite rejectInvite(User inviteRecipient, UUID inviteId) {
        log.info("rejectInvite[1]: reject friend invitation with id: {}", inviteId);

        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME, Constants.ID_FIELD, inviteId));

        if (!inviteRecipient.equals(invite.getRecipient())) {
            log.error("acceptInvite[1]: You don't have permissions for this action!");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        invite.setStatus(InviteStatus.REJECTED);
        inviteRepository.delete(invite);
        log.info("acceptInvite[2]: invite: {} was rejected", invite);
        return invite;
    }

    @Override
    public List<Invite> getAllInvites(User user) {
        log.info("getAllInvites[1]: getting user invites: {}", user);
        List<Invite> invites = inviteRepository.findAllByRecipient(user);
        log.info("getAllInvites[2]: invites: {}", invites);
        return invites;
    }

    @Override
    public Invite deleteInvite(User user, UUID inviteId) {
        log.info("deleteInvite[1]: deleting invite with id: {}", inviteId);

        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME, Constants.ID_FIELD, inviteId));

        if (!user.equals(invite.getSender())) {
            log.error("deleteInvite[1]: You don't have permissions for this action!");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        inviteRepository.delete(invite);
        invite.setStatus(InviteStatus.DELETED_BY_SENDER);
        log.info("deleteInvite[2]: invite {} was successfully deleted", invite);
        return invite;
    }

    @Override
    public Invite getInviteBySenderAndRecipient(UUID senderId, UUID recipientId) {
        log.info("getInviteBySenderAndRecipient[1]: getting invite sender: {}, recipient: {}", senderId, recipientId);
        User sender = userService.getUserById(senderId);
        User recipient = userService.getUserById(recipientId);
        return inviteRepository.findInviteBySenderAndRecipient(sender, recipient)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.INVITE_RESOURCE_NAME));
    }
}
