package com.soundhub.api.service;

import com.soundhub.api.dto.request.GroupChatRequest;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.User;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    Chat createChat(User sender, UUID recipientId) throws ResourceNotFoundException;

    Chat getChatById(UUID chatId) throws ResourceNotFoundException;

    List<Chat> findAllChatsByUserId(UUID userId) throws ResourceNotFoundException;

    Chat createGroup(GroupChatRequest req, User creator);

    Chat addUserToGroup(UUID chatId, UUID userId) throws ResourceNotFoundException;

    Chat removeFromGroup(UUID chatId, UUID userId) throws ResourceNotFoundException;

    Chat renameGroup(UUID chatId, String groupName, User user);

    UUID deleteChat(UUID chatId) throws ResourceNotFoundException;
}
