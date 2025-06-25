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
        // 메시지 타입에 따라 키 설정
        String key = message.getClass().getSimpleName();
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka 메시지 전송 성공 - Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                    topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Kafka 메시지 전송 실패 - Topic: {}, Key: {}, Error: {}", topic, key, ex.getMessage(), ex);
            }
        });
    }
}