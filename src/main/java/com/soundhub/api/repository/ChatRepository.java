package com.soundhub.api.repository;

import com.soundhub.api.model.Chat;
import com.soundhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
//    Optional<Chat> findBySenderIdAndRecipientId(UUID senderId, UUID recipientId);

    @Query("SELECT c FROM Chat c JOIN c.participants u WHERE u.id=:user_id")
    List<Chat> findChatsByUserId(@Param("user_id") UUID userId);

    @Query("SELECT c FROM Chat c WHERE c.isGroup = false AND :sender MEMBER OF c.participants AND :recipient MEMBER OF c.participants")
    Optional<Chat> findSingleChatByUsers(@Param("sender") User sender, @Param("recipient") User recipient);

}
