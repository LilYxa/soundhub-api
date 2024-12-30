package com.soundhub.api.service.invite;

import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import com.soundhub.api.service.invite.strategy.DeleteInviteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteInviteStrategyTest {

    @Mock
    private InviteRepository inviteRepository;

    @InjectMocks
    private DeleteInviteStrategy deleteInviteStrategy;

    private Invite validInvite;
    private User sender;
    private UUID inviteId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = User.builder()
                .id(UUID.randomUUID())
                .email("sender@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        inviteId = UUID.randomUUID();
        validInvite = Invite.builder()
                .id(inviteId)
                .sender(sender)
                .recipient(User.builder().id(UUID.randomUUID()).email("recipient@example.com").build())
                .status(InviteStatus.CONSIDERED)
                .build();
    }

    @Test
    void testExecute_Success() {
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(validInvite));

        Invite result = deleteInviteStrategy.execute(sender, inviteId);

        assertEquals(InviteStatus.DELETED_BY_SENDER, result.getStatus(), "The invite status should be DELETED_BY_SENDER.");
        verify(inviteRepository, times(1)).delete(validInvite);
    }

    @Test
    void testExecute_InviteNotFound() {
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleteInviteStrategy.execute(sender, inviteId),
                "ResourceNotFoundException should be thrown when invite is not found.");
    }

    @Test
    void testExecute_InvalidSender() {
        when(inviteRepository.findById(inviteId)).thenReturn(Optional.of(validInvite));

        User wrongSender = User.builder()
                .id(UUID.randomUUID())
                .email("wrongsender@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        assertThrows(ApiException.class, () -> deleteInviteStrategy.execute(wrongSender, inviteId),
                "ApiException should be thrown when the sender does not match.");
    }
}
