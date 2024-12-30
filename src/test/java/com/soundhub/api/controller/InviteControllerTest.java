package com.soundhub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soundhub.api.enums.InviteStatus;
import com.soundhub.api.enums.Role;
import com.soundhub.api.model.Invite;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.InviteRepository;
import com.soundhub.api.security.JwtService;
import com.soundhub.api.service.InviteService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class InviteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private InviteRepository inviteRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private InviteService inviteService;

    @InjectMocks
    private InviteController inviteController;

    private UUID senderId;
    private UUID recipientId;
    private User sender;
    private User recipient;
    private Invite inviteConsidered, inviteAccepted, inviteRejected, inviteDeleted;
    private String jwtToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inviteController).build();

        senderId = UUID.randomUUID();
        recipientId = UUID.randomUUID();

        User mockUser = Mockito.mock(User.class);

        sender = User.builder()
                .id(senderId)
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Vasya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2000, 5, 15))
                .role(Role.ROLE_USER)
                .build();

        recipient = User.builder()
                .id(recipientId)
                .email("petya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Petya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2002, 12, 15))
                .role(Role.ROLE_USER)
                .build();

        inviteConsidered = Invite.builder()
                .id(UUID.randomUUID())
                .createdDateTime(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.CONSIDERED)
                .build();

        inviteAccepted = Invite.builder()
                .id(UUID.randomUUID())
                .createdDateTime(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.ACCEPTED)
                .build();

        inviteRejected = Invite.builder()
                .id(UUID.randomUUID())
                .createdDateTime(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.REJECTED)
                .build();

        inviteDeleted = Invite.builder()
                .id(UUID.randomUUID())
                .createdDateTime(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .status(InviteStatus.DELETED_BY_SENDER)
                .build();

        jwtToken = "Bearer " + jwtService.generateToken(sender);
        log.debug("setUp[1]: jwtToken {}", jwtToken);
        when(userService.getCurrentUser()).thenReturn(sender);
        when(userService.getUserById(any(UUID.class))).thenReturn(recipient);
    }

    @Test
    @WithMockUser
    public void testCreateInvite() throws Exception {
        when(inviteService.createInvite(any(User.class), any(User.class))).thenReturn(inviteConsidered);
        log.debug("testCreateInvite[1]: sender {}", sender.toString());
        log.debug("testCreateInvite[2]: recipient {}", recipient.toString());
        log.debug("testCreateInvite[3]: invite status {}", inviteConsidered.getStatus().toString());

        MvcResult result = mockMvc.perform(post("/api/v1/invites/create/{recipientId}", recipientId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inviteConsidered.getId().toString()))
                .andExpect(jsonPath("$.sender.id").value(sender.getId().toString()))
                .andExpect(jsonPath("$.recipient.id").value(recipient.getId().toString()))
                .andExpect(jsonPath("$.status").value(InviteStatus.CONSIDERED.toString()))
                .andReturn();

        log.debug("testCreateInvite[4]: response result: {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).createInvite(any(User.class), any(User.class));
    }

    @Test
    public void testAcceptInvite() throws Exception {
        when(inviteService.acceptInvite(any(User.class), any(UUID.class))).thenReturn(inviteAccepted);
        log.debug("testAcceptInvite[1]: sender {}", sender.toString());
        log.debug("testAcceptInvite[2]: recipient {}", recipient.toString());
        log.debug("testAcceptInvite[3]: invite status {}", inviteAccepted.getStatus().toString());

        MvcResult result = mockMvc.perform(post("/api/v1/invites/accept/{inviteId}", inviteAccepted.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inviteAccepted.getId().toString()))
                .andExpect(jsonPath("$.sender.id").value(sender.getId().toString()))
                .andExpect(jsonPath("$.recipient.id").value(recipient.getId().toString()))
                .andExpect(jsonPath("$.status").value(InviteStatus.ACCEPTED.toString()))
                .andReturn();

        log.debug("testAcceptInvite[4]: response result: {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).acceptInvite(any(User.class), any(UUID.class));
    }

    @Test
    public void testRejectInvite() throws Exception {
        when(inviteService.rejectInvite(any(User.class), any(UUID.class))).thenReturn(inviteRejected);
        log.debug("testRejectInvite[1]: sender {}", sender.toString());
        log.debug("testRejectInvite[2]: recipient {}", recipient.toString());
        log.debug("testRejectInvite[3]: invite status {}", inviteAccepted.getStatus().toString());

        MvcResult result = mockMvc.perform(post("/api/v1/invites/reject/{inviteId}", inviteRejected.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inviteRejected.getId().toString()))
                .andExpect(jsonPath("$.sender.id").value(sender.getId().toString()))
                .andExpect(jsonPath("$.recipient.id").value(recipient.getId().toString()))
                .andExpect(jsonPath("$.status").value(InviteStatus.REJECTED.toString()))
                .andReturn();

        log.debug("testRejectInvite[4]: response result: {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).rejectInvite(any(User.class), any(UUID.class));
    }

    @Test
    public void testGetAllInvites() throws Exception {
        List<Invite> invitesList = List.of(inviteAccepted, inviteRejected, inviteConsidered);
        when(inviteService.getAllInvites(any(User.class))).thenReturn(invitesList);
        log.debug("testGetAllInvites[1]: sender {}", sender.toString());
        log.debug("testGetAllInvites[2]: recipient {}", recipient.toString());
        log.debug("testGetAllInvites[3]: invitesList {}", invitesList);

        MvcResult result = mockMvc.perform(get("/api/v1/invites")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(inviteAccepted.getId().toString()))
                .andExpect(jsonPath("$[0].sender.id").value(inviteAccepted.getSender().getId().toString()))
                .andExpect(jsonPath("$[0].recipient.id").value(inviteAccepted.getRecipient().getId().toString()))
                .andExpect(jsonPath("$[0].status").value(InviteStatus.ACCEPTED.toString()))
                .andExpect(jsonPath("$[1].id").value(inviteRejected.getId().toString()))
                .andExpect(jsonPath("$[1].sender.id").value(inviteRejected.getSender().getId().toString()))
                .andExpect(jsonPath("$[1].recipient.id").value(inviteRejected.getRecipient().getId().toString()))
                .andExpect(jsonPath("$[1].status").value(InviteStatus.REJECTED.toString()))
                .andExpect(jsonPath("$[2].id").value(inviteConsidered.getId().toString()))
                .andExpect(jsonPath("$[2].sender.id").value(inviteConsidered.getSender().getId().toString()))
                .andExpect(jsonPath("$[2].recipient.id").value(inviteConsidered.getRecipient().getId().toString()))
                .andExpect(jsonPath("$[2].status").value(InviteStatus.CONSIDERED.toString()))
                .andReturn();

        log.debug("testGetAllInvites[4]: response result: {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).getAllInvites(any(User.class));
    }

    @Test
    public void testDeleteInvite() throws Exception {
        when(inviteService.deleteInvite(any(User.class), any(UUID.class))).thenReturn(inviteDeleted);
        log.debug("testDeleteInvite[1]: sender {}", sender.toString());
        log.debug("testDeleteInvite[2]: recipient {}", recipient.toString());
        log.debug("testDeleteInvite[3]: inviteDeleted {}", inviteDeleted.toString());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invites/{inviteId}", inviteDeleted.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inviteDeleted.getId().toString()))
                .andExpect(jsonPath("$.sender.id").value(inviteDeleted.getSender().getId().toString()))
                .andExpect(jsonPath("$.recipient.id").value(inviteDeleted.getRecipient().getId().toString()))
                .andExpect(jsonPath("$.status").value(InviteStatus.DELETED_BY_SENDER.toString()))
                .andReturn();

        log.debug("testDeleteInvite[4]: response result {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).deleteInvite(any(User.class), any(UUID.class));
    }

    @Test
    public void testGetInviteBySenderAndRecipient() throws Exception {
        when(inviteService.getInviteBySenderAndRecipient(any(UUID.class), any(UUID.class))).thenReturn(inviteConsidered);
        log.debug("testGetInviteBySenderAndRecipient[1]: sender {}", sender.toString());
        log.debug("testGetInviteBySenderAndRecipient[2]: recipient {}", recipient.toString());
        log.debug("testGetInviteBySenderAndRecipient[3]: inviteConsidered {}", inviteConsidered.toString());

        MvcResult result = mockMvc.perform(get("/api/v1/invites/{senderId}/{recipientId}", sender.getId(), recipient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inviteConsidered.getId().toString()))
                .andExpect(jsonPath("$.sender.id").value(inviteConsidered.getSender().getId().toString()))
                .andExpect(jsonPath("$.recipient.id").value(inviteConsidered.getRecipient().getId().toString()))
                .andExpect(jsonPath("$.status").value(InviteStatus.CONSIDERED.toString()))
                .andReturn();

        log.debug("testGetInviteBySenderAndRecipient[4]: response result {}", result.getResponse().getContentAsString());

        verify(inviteService, times(1)).getInviteBySenderAndRecipient(any(UUID.class), any(UUID.class));
    }
}

