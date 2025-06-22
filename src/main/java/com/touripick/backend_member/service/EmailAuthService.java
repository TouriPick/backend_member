package com.touripick.backend_member.service;

import com.touripick.backend_member.domain.event.EmailAuthEvent;
import com.touripick.backend_member.event.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaMessageProducer kafkaMessageProducer;

    public void requestEmailAuth(String email) {
        String code = generateRandomCode();
        String redisKey = "email:auth:" + email;
        
        // 기존 인증코드가 있다면 삭제
        redisTemplate.delete(redisKey);
        
        // 인증코드 10분간 저장
        redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(10));
        
        log.info("이메일 인증 요청: {}, 코드: {}", email, code);
        
        // Kafka로 이메일 발송 이벤트 전송
        kafkaMessageProducer.send("email-auth-send", new EmailAuthEvent(email, code));
    }

    private String generateRandomCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자
    }

    public boolean verifyEmailCode(String email, String inputCode) {
        String redisKey = "email:auth:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            log.warn("이메일 인증코드 만료 또는 존재하지 않음: {}", email);
            return false;
        }
        
        boolean isValid = inputCode.equals(storedCode);
        
        if (isValid) {
            // 인증 성공 시 Redis에서 코드 삭제
            redisTemplate.delete(redisKey);
            log.info("이메일 인증 성공: {}", email);
        } else {
            log.warn("이메일 인증 실패: {}, 입력코드: {}, 저장된코드: {}", email, inputCode, storedCode);
        }
        
        return isValid;
    }
}