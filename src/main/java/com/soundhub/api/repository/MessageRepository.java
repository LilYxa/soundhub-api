package com.soundhub.api.repository;

import com.soundhub.api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    List<Message> findByChatId(@Param("chatId") UUID chatId);

    @Override
    @Modifying
    @Query("delete Message m where m.id = ?1")
    void deleteById(UUID uuid);
}
