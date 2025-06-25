package com.touripick.backend_member.event.consumer;

import com.touripick.backend_member.domain.event.EmailAuthEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "email-auth-send")
    public void sendAuthEmail(EmailAuthEvent event, Acknowledgment ack) {
        try {
            log.info("이메일 인증 메일 발송 시작: {}", event.getEmail());
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getEmail());
            message.setSubject("[TourPick] 이메일 인증 코드");
            message.setText("안녕하세요!\n\n회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                          "인증코드: " + event.getCode() + "\n\n" +
                          "이 코드는 10분간 유효합니다.\n" +
                          "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                          "감사합니다.\nTourPick 팀");
            
            mailSender.send(message);
            
            log.info("이메일 인증 메일 발송 완료: {}", event.getEmail());
            
            // 수동 커밋
            ack.acknowledge();
            
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}, Error: {}", event.getEmail(), e.getMessage(), e);
            // 에러 발생 시에도 커밋하여 중복 발송 방지
            ack.acknowledge();
        }
    }
}