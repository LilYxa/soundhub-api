package com.soundhub.api.service.invite;

import com.soundhub.api.Constants;
import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import com.soundhub.api.service.UserService;
import com.soundhub.api.service.invite.strategy.AcceptInviteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcceptInviteStrategyTest {

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AcceptInviteStrategy acceptInviteStrategy;

    private User recipient;
    private User sender;
    private Invite invite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        recipient = User.builder()
                .id(UUID.randomUUID())
                .email("recipient@example.com")
                .build();

        sender = User.builder()
                .id(UUID.randomUUID())
                .email("sender@example.com")
                .build();

        invite = Invite.builder()
                .id(UUID.randomUUID())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.CONSIDERED)
                .build();
    }

    @Test
    void execute_ShouldAcceptInvite_WhenRecipientIsCorrect() {
        UUID inviteId = invite.getId();
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(invite));

        Invite result = acceptInviteStrategy.execute(recipient, inviteId);

        assertNotNull(result);
        assertEquals(InviteStatus.ACCEPTED, result.getStatus());
        verify(userService, times(1)).addFriend(sender.getId());
        verify(inviteRepository, times(1)).delete(invite);
    }

    @Test
    void execute_ShouldThrowResourceNotFoundException_WhenInviteNotFound() {
        UUID inviteId = UUID.randomUUID();
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> acceptInviteStrategy.execute(recipient, inviteId));
        assertEquals(Constants.INVITE_RESOURCE_NAME, exception.getResourceName());
        assertEquals(Constants.ID_FIELD, exception.getFieldName());
        verifyNoInteractions(userService);
    }

    @Test
    void execute_ShouldThrowApiException_WhenRecipientDoesNotMatch() {
        UUID inviteId = invite.getId();
        User wrongRecipient = User.builder().id(UUID.randomUUID()).build();
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(invite));

        ApiException exception = assertThrows(ApiException.class,
                () -> acceptInviteStrategy.execute(wrongRecipient, inviteId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        assertEquals(Constants.PERMISSION_MESSAGE, exception.getMessage());
        verifyNoInteractions(userService);
        verify(inviteRepository, never()).delete(invite);
    }
}
