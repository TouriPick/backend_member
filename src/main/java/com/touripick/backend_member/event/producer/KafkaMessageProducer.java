package com.touripick.backend_member.event.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka 메시지 전송 성공 - Topic: {}, Partition: {}, Offset: {}", 
                    topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Kafka 메시지 전송 실패 - Topic: {}, Error: {}", topic, ex.getMessage(), ex);
            }
        });
    }
}