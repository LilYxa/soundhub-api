package com.soundhub.api.service.impl;

import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message sendMessage(SendMessageRequest request);

    List<Message> findMessagesByChatId(UUID chatId, User reqUser);

    Message findMessageById(UUID messageId);

    UUID deleteMessageById(UUID messageId, User reqUser);

    Message changeMessage(UUID messageId, String newContent, User reqUser);

    Message markMessageAsRead(UUID messageId);
}
