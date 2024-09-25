package com.soundhub.api.controller;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.ChatNotificationDto;
import com.soundhub.api.dto.request.SendMessageRequest;
import com.soundhub.api.dto.response.UnreadMessagesResponse;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.model.Message;
import com.soundhub.api.model.User;
import com.soundhub.api.service.MessageService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@Slf4j
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
                "/queue/messages/" + sendMessageRequest.getChatId().toString(),
                ChatNotificationDto.builder()
                        .id(savedMessage.getId())
                        .chatId(savedMessage.getChat().getId())
                        .replyToMessageId(savedMessage.getReplyToMessageId())
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
        String userId = headerAccessor.getFirstNativeHeader(Constants.DELETER_ID_HEADER);
        if (userId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, Constants.PERMISSION_MESSAGE);
        }
        User user = userService.getUserById(UUID.fromString(userId));

        UUID deletedMsgId = messageService.deleteMessageById(messageId, user);
        messagingTemplate.convertAndSend("/queue/messages/delete", deletedMsgId);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Page<Message>> getChatMessages(
        @PathVariable UUID chatId,
        @RequestParam(defaultValue = Constants.DEFAULT_MESSAGE_PAGE) int page,
        @RequestParam(defaultValue = Constants.DEFAULT_MESSAGE_PAGE_SIZE) int size,
        @RequestParam(defaultValue = "timestamp") String sort,
        @RequestParam(defaultValue = "desc") String order
    ) {
        User currentUser = userService.getCurrentUser();
        Page<Message> chatMessages = messageService.findPagedMessagesByChatId(
            chatId, currentUser, page, size, sort, order
        );

        log.debug("[1] MessageController[getChatMessages] -> current user: {}", currentUser);
        log.debug("[2] MessageController[getChatMessages] -> chatMessages: {}", chatMessages);

        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<UnreadMessagesResponse> getUnreadMessages() {
        return new ResponseEntity<>(messageService.getUnreadMessages(), HttpStatus.OK);
    }


    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable UUID messageId) {
        Message message = messageService.findMessageById(messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
