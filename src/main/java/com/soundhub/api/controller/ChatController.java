package com.soundhub.api.controller;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.response.ApiResponse;
import com.soundhub.api.dto.request.GroupChatRequest;
import com.soundhub.api.dto.request.SingleChatRequest;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.User;
import com.soundhub.api.service.ChatService;
import com.soundhub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @PostMapping("/single")
    public ResponseEntity<Chat> createChat(@RequestBody SingleChatRequest singleChatRequest) {
        User currentUser = userService.getCurrentUser();
        Chat chat = chatService.createChat(currentUser, singleChatRequest.getRecipientId());
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupChat(@RequestBody GroupChatRequest groupChatRequest) {
        User currentUser = userService.getCurrentUser();
        Chat groupChat = chatService.createGroup(groupChatRequest, currentUser);
        return new ResponseEntity<>(groupChat, HttpStatus.OK);
    }

    @GetMapping("{chatId}")
    public ResponseEntity<Chat> findChatById(@PathVariable UUID chatId) {
        Chat chat = chatService.getChatById(chatId);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Chat>> findChatsByUserId() {
        User currentUser = userService.getCurrentUser();
        List<Chat> currentUserChats = chatService.findAllChatsByUserId(currentUser.getId());
        return new ResponseEntity<>(currentUserChats, HttpStatus.OK);
    }

    @PutMapping("{chatId}/add/{userId}")
    public ResponseEntity<Chat> addUserToGroup(@PathVariable UUID chatId, @PathVariable UUID userId) {
        Chat chat = chatService.addUserToGroup(chatId, userId);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @PutMapping("{chatId}/remove/{userId}")
    public ResponseEntity<Chat> removeUserFromGroup(@PathVariable UUID chatId, @PathVariable UUID userId) {
        Chat chat = chatService.removeFromGroup(chatId, userId);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<ApiResponse> deleteChat(@PathVariable UUID chatId) {
        UUID deletedChatId = chatService.deleteChat(chatId);
        ApiResponse response = new ApiResponse(true, String.format(Constants.CHAT_DELETE_SUCCESS_MSG, deletedChatId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/rename/{newName}")
    public ResponseEntity<Chat> renameGroup(@PathVariable UUID chatId, @PathVariable String newName) {
        User user = userService.getCurrentUser();
        Chat chat = chatService.renameGroup(chatId, newName, user);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }


}
