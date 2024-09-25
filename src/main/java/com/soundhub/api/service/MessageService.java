package com.soundhub.api.service;

import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.dto.response.UnreadMessagesResponse;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface MessageService {
    Message sendMessage(SendMessageRequest request);

    Page<Message> findPagedMessagesByChatId(
        UUID chatId,
        User reqUser,
        int page,
        int size,
        String sort,
        String order
    );

    UnreadMessagesResponse getUnreadMessages();

    Message findMessageById(UUID messageId);

    UUID deleteMessageById(UUID messageId, User reqUser);

    Message changeMessage(UUID messageId, String newContent, User reqUser);

    Message markMessageAsRead(UUID messageId);
}
