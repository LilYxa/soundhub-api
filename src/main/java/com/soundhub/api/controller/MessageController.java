package com.soundhub.api.controller;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.ChatNotificationDto;
import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;
import com.soundhub.api.service.UserService;
import com.soundhub.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload SendMessageRequest sendMessageRequest) {
        Message savedMessage = messageService.sendMessage(sendMessageRequest);

        messagingTemplate.convertAndSend(
                "/queue/messages/"+sendMessageRequest.getChatId().toString(),
                ChatNotificationDto.builder()
                        .id(savedMessage.getId())
                        .chatId(savedMessage.getChat().getId())
                        .senderId(savedMessage.getSender().getId())
                        .content(savedMessage.getContent())
                        .build()
                );
    }

    @MessageMapping("/message/read/{messageId}")
    public void markMessageAsRead(@DestinationVariable UUID messageId) {
        Message message = messageService.markMessageAsRead(messageId);
        messagingTemplate.convertAndSend("/queue/messages/read", message);
    }

    @MessageMapping("/message/delete/{messageId}")
    public void deleteMessage(@DestinationVariable UUID messageId, SimpMessageHeaderAccessor headerAccessor) {
//        User currentUser = userService.getCurrentUser();
        String userId = headerAccessor.getFirstNativeHeader(Constants.DELETER_ID_HEADER);
        if (userId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }
        User user = userService.getUserById(UUID.fromString(userId));

        UUID deletedMsgId = messageService.deleteMessageById(messageId, user);
        messagingTemplate.convertAndSend("/queue/messages/delete", deletedMsgId);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable UUID chatId) {
        User currentUser = userService.getCurrentUser();
        List<Message> chatMessages = messageService.findMessagesByChatId(chatId, currentUser);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable UUID messageId) {
        Message message = messageService.findMessageById(messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
