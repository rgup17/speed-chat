package com.speedchat.batcher.repositories;

import com.speedchat.batcher.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

}
