package com.speedchat.batcher.kafka;

import com.google.gson.Gson;
import com.speedchat.batcher.models.ChatMessage;
import com.speedchat.batcher.services.MessageBatchingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageConsumer {

    private final Logger LOGGER = LogManager.getLogger(MessageConsumer.class);
    private final MessageBatchingService messageBatchingService;
    private final Gson gson;

    public MessageConsumer(MessageBatchingService messageBatchingService, Gson gson) {
        this.messageBatchingService = messageBatchingService;
        this.gson = gson;
    }

    @KafkaListener(topics = {"${kafka-props.chat-messages-topic}"}, groupId = "${kafka-props.chat-messages-group}", containerFactory = "messageConcurrentKafkaListenerContainerFactory")
    @Async("cachedThreadPool")
    void consume(@Payload List<String> messages,
                 @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                 @Header(KafkaHeaders.OFFSET) List<Long> offsets,
                 Acknowledgment acknowledgment
                 )
    {
        try {
            LOGGER.info("Starting message batching");
            List<ChatMessage> batchMessages = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                try {
                    ChatMessage chatMessage = gson.fromJson(messages.get(i), ChatMessage.class);
                    batchMessages.add(chatMessage);
                } catch (Exception e) {
                    LOGGER.info("Error parsing message at partition-offset='{}'",partitions.get(i) + "-" + offsets.get(i));
                }
                LOGGER.info("received message with partition-offset='{}'",partitions.get(i) + "-" + offsets.get(i));
            }
            messageBatchingService.saveMessagesBatch(batchMessages, String.format("%d-%d to %d-%d",
                    partitions.getFirst(), offsets.getFirst(),
                    partitions.getLast(), offsets.getLast()
            ));
        }
        catch (Exception e) {
            LOGGER.info("Failed to parse message -> {}", e.getMessage());
        }
        acknowledgment.acknowledge();
    }

}
