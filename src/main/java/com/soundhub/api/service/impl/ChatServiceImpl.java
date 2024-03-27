package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.request.GroupChatRequest;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.ChatRepository;
import com.soundhub.api.service.ChatService;
import com.soundhub.api.service.UserService;
import com.soundhub.api.util.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Chat createChat(User sender, UUID recipientId) throws ResourceNotFoundException {
        log.info("createChat[1]: creating a chat, sender ID: {}, recipient ID: {}", sender.getId(), recipientId);
        User recipient = userMapper.userDtoToUser(userService.getUserById(recipientId));

        return chatRepository.findSingleChatByUsers(sender, recipient)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .createdBy(sender)
                            .isGroup(false)
                            .participants(List.of(sender, recipient))
                            .build();
                    return chatRepository.save(newChat);
                });
    }

    @Override
    public Chat getChatById(UUID chatId) throws ResourceNotFoundException {
        log.info("getChatById: chatId: {}", chatId);
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CHAT_RESOURCE_NAME, Constants.ID_FIELD, chatId));
    }

    @Override
    public List<Chat> findAllChatsByUserId(UUID userId) throws ResourceNotFoundException {
        User user = userMapper.userDtoToUser(userService.getUserById(userId));

        return chatRepository.findChatsByUserId(user.getId());
    }

    @Override
    public Chat createGroup(GroupChatRequest req, User creator) {
        List<User> groupParticipants = new ArrayList<>();
        req.getUserIds().forEach(userId -> groupParticipants.add(userMapper.userDtoToUser(userService.getUserById(userId))));

        Chat groupChat = Chat.builder()
                .createdBy(creator)
                .isGroup(true)
                .chatImage(req.getChatImage())
                .participants(groupParticipants)
                .chatName(req.getGroupName())
                .build();
        return chatRepository.save(groupChat);
    }

    @Override
    public Chat addUserToGroup(UUID chatId, UUID userId) throws ResourceNotFoundException {
        log.info("addUserToGroup[1]: adding user with id: {} to chat group", userId);
        Chat chat = getChatById(chatId);
        User chatOwner = userService.getCurrentUser();

        if (chatOwner.equals(chat.getCreatedBy())) {
            User user = userMapper.userDtoToUser(userService.getUserById(userId));
            chat.getParticipants().add(user);
            chatRepository.save(chat);
        } else {
            log.error("addUserToGroup[1]: error");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        return chat;
    }

    @Override
    public Chat removeFromGroup(UUID chatId, UUID userId) throws ResourceNotFoundException {
        log.info("removeFromGroup[1]: removing user with id: {} from chat group: {}", userId, chatId);
        Chat chat = getChatById(chatId);
        User chatOwner = userService.getCurrentUser();

        if (chatOwner.equals(chat.getCreatedBy())) {
            User user = userMapper.userDtoToUser(userService.getUserById(userId));
            chat.getParticipants().remove(user);
            chatRepository.save(chat);
        } else {
            log.error("removeFromGroup[1]: error");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        return chat;
    }

    @Override
    public Chat renameGroup(UUID chatId, String groupName, User user) {
        Chat chat = getChatById(chatId);
        log.info("renameGroup[1]: rename chat group from {} to {}", chat.getChatName(), groupName);

        if (user.equals(chat.getCreatedBy())) {
            chat.setChatName(groupName);
            chatRepository.save(chat);
        } else {
            log.error("renameGroup[1]: error");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }

        return chat;
    }

    @Override
    public UUID deleteChat(UUID chatId) throws ResourceNotFoundException {
        log.info("deleteCHat[1]: deleting chat with id: {}", chatId);
        Chat chat = getChatById(chatId);
        
        if (userService.getCurrentUser().equals(chat.getCreatedBy())) {
            chatRepository.delete(chat);
        } else {
            log.error("deleteChat[1]: error");
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }
        return chat.getId();
    }


}
