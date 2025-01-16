package com.speedchat.batcher.services;

import com.speedchat.batcher.models.ChatMessage;
import com.speedchat.batcher.repositories.MessageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageBatchingService {
    private final Logger LOGGER = LogManager.getLogger(MessageBatchingService.class);
    private final MessageRepository messageRepository;

    public MessageBatchingService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Async("cachedThreadPool")
    public void saveMessagesBatch(List<ChatMessage> messages, String range) {
        try {
            messageRepository.saveAll(messages);
            messages.forEach(message -> {
                LOGGER.info("The message is : {}", messages);
            });
            LOGGER.info("Successfully saved message batch of size {}", range);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }
}
