package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.dto.response.UnreadMessagesResponse;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.MessageRepository;
import com.soundhub.api.service.ChatService;
import com.soundhub.api.service.MessageService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Override
    public Message sendMessage(SendMessageRequest request) {
        User user = userService.getUserById(request.getUserId());
        Chat chat = chatService.getChatById(request.getChatId());

        Message message = Message.builder()
                .chat(chat)
                .sender(user)
                .content(request.getContent())
                .replyToMessageId(request.getReplyToMessageId())
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Page<Message> findPagedMessagesByChatId(
        UUID chatId,
        User reqUser,
        int page,
        int size,
        String sort,
        String order
    ) {
        Chat chat = chatService.getChatById(chatId);
        int adjustedPage = (page > 0) ? page - 1 : 0;

        Sort sortType = order.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();

        PageRequest pageRequest = PageRequest.of(adjustedPage, size, sortType);
        Page<Message> pages = messageRepository.findByChat_Id(chatId, pageRequest);

        if (adjustedPage > pages.getTotalPages())
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    String.format(Constants.MESSAGE_PAGE_NOT_FOUND, pages.getTotalPages())
            );

        if (!chat.getParticipants().contains(reqUser)) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.CHAT_NOT_CONTAINS_USER);
        }

        return pages;
    }

    @Override
    public UnreadMessagesResponse getUnreadMessages() {
        UUID currentUserId = userService.getCurrentUser().getId();
        List<Message> unreadMessages = messageRepository.findAll()
                .stream()
                .filter(msg -> msg.getSender().getId() != currentUserId && !msg.getIsRead())
                .toList();

        return UnreadMessagesResponse.builder()
                .messages(unreadMessages)
                .count(unreadMessages.size())
                .build();
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MESSAGE_RESOURCE_NAME, Constants.ID_FIELD, messageId));
    }

    @Override
    @Transactional
    public UUID deleteMessageById(UUID messageId, User reqUser) {
        Message message = findMessageById(messageId);

        if (message.getSender().getId().equals(reqUser.getId())) {
            messageRepository.deleteById(message.getId());
        } else {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }
        return message.getId();
    }

    @Override
    public Message changeMessage(UUID messageId, String newContent, User reqUser) {
        Message message = findMessageById(messageId);

        if (message.getSender().getId().equals(reqUser.getId())) {
            message.setContent(newContent);
            messageRepository.save(message);
        } else {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }
        return message;
    }

    @Override
    public Message markMessageAsRead(UUID messageId) {
        Message message = findMessageById(messageId);
        message.setIsRead(true);
        messageRepository.save(message);

        return message;
    }
}
