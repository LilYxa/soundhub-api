package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.MessageRepository;
import com.soundhub.api.service.ChatService;
import com.soundhub.api.service.UserService;
import com.soundhub.api.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Message sendMessage(SendMessageRequest request) {
        User user = userMapper.userDtoToUser(userService.getUserById(request.getUserId()));
        Chat chat = chatService.getChatById(request.getChatId());

        Message message = Message.builder()
                .chat(chat)
                .sender(user)
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public List<Message> findMessagesByChatId(UUID chatId, User reqUser) {
        Chat chat = chatService.getChatById(chatId);

        if (!chat.getParticipants().contains(reqUser)) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.CHAT_NOT_CONTAINS_USER);
        }

        return messageRepository.findByChatId(chatId) == null
                ? new ArrayList<>()
                : messageRepository.findByChatId(chatId);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MESSAGE_RESOURCE_NAME, Constants.ID_FIELD, messageId));
    }

    @Override
    public UUID deleteMessageById(UUID messageId, User reqUser) {
        Message message = findMessageById(messageId);

        if (message.getSender().getId().equals(reqUser.getId())) {
            messageRepository.delete(message);
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
        return message;
    }
}
