package com.soundhub.api.aspect;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.PostDto;
import com.soundhub.api.model.Chat;
import com.soundhub.api.model.Post;
import com.soundhub.api.model.User;
import lombok.NonNull;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class FileAspect {

    @Value("${base.url}")
    private String baseUrl;

    private void modifyAvatarUrl(User user) {
        if (user != null && user.getAvatarUrl() != null) {
            String userAvatar = user.getAvatarUrl();
            if (!userAvatar.contains(baseUrl)) {
                user.setAvatarUrl(baseUrl + Constants.FILE_PATH_PART + userAvatar);
            }
        }
    }

    private List<String> modifyFileUrls(List<String> urls) {
        return urls.stream()
                .map(url -> (url != null && !url.contains(baseUrl)) ? baseUrl + Constants.FILE_PATH_PART + url : url)
                .toList();
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(com.soundhub.api.model.User *(..))",
            returning = "user"
    )
    public void modifyUser(User user) {
        modifyAvatarUrl(user);
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(com.soundhub.api.model.Chat *(..))",
            returning = "chat"
    )
    public void modifyChat(Chat chat) {
        if (chat != null) {
            chat.getParticipants().forEach(this::modifyAvatarUrl);
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(com.soundhub.api.model.Post *(..))",
            returning = "post"
    )
    public void modifyPostUrl(Post post) {
        if (post != null) {
            post.setImages(modifyFileUrls(post.getImages()));
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(com.soundhub.api.dto.PostDto *(..))",
            returning = "postDto"
    )
    public void modifyPostDto(PostDto postDto) {
        if (postDto != null) {
            postDto.setImages(modifyFileUrls(postDto.getImages()));
        }
    }


    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(java.util.List<com.soundhub.api.model.Post> *(..))",
            returning = "posts"
    )
    public void modifyPostUrlsForList(@NonNull List<Post> posts) {
        posts.forEach(this::modifyPostUrl);
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.*.*(..)) " +
                    "&& execution(java.util.List<com.soundhub.api.model.Chat> *(..))",
            returning = "chats"
    )
    public void modifyChatList(@NonNull List<Chat> chats) {
        chats.forEach(this::modifyChat);
    }

    @AfterReturning(
            pointcut = "execution(* com.soundhub.api.service.UserService.get*(..)) " +
                    "&& execution(java.util.List<com.soundhub.api.model.User> *(..))",
            returning = "users"
    )
    public void modifyAvatarUrlsForList(@NonNull List<User> users) {
        users.forEach(this::modifyAvatarUrl);
    }
}
