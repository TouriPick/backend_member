package com.touripick.backend_member.service;

import com.touripick.backend_member.common.exception.BadParameter;
import com.touripick.backend_member.common.exception.NotFound;
import com.touripick.backend_member.common.type.ActionAndId;
import com.touripick.backend_member.domain.SiteUser;
import com.touripick.backend_member.domain.dto.SiteUserInfoDto;
import com.touripick.backend_member.domain.dto.SiteUserLoginDto;
import com.touripick.backend_member.domain.dto.SiteUserRefreshDto;
import com.touripick.backend_member.domain.dto.SiteUserRegisterDto;
import com.touripick.backend_member.domain.event.SiteUserInfoEvent;
import com.touripick.backend_member.domain.repository.SiteUserRepository;
import com.touripick.backend_member.event.producer.KafkaMessageProducer;
import com.touripick.backend_member.secret.hash.SecureHashUtils;
import com.touripick.backend_member.secret.jwt.TokenGenerator;
import com.touripick.backend_member.secret.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사이트 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final TokenGenerator tokenGenerator;
    private final KafkaMessageProducer kafkaMessageProducer;

    /**
     * 새로운 사용자를 등록합니다.
     * {@link SiteUserRegisterDto}로부터 {@link SiteUser} 엔티티를 생성하여 저장합니다.
     * 이 메서드는 트랜잭션 내에서 실행됩니다.
     *
     * @param registerDto 사용자 등록 정보를 담고 있는 DTO
     */
    @Transactional
    public void registerUser(SiteUserRegisterDto registerDto) {
        // TODO: 사용자 ID 중복 체크 로직 추가 필요
        SiteUser siteUser = registerDto.toEntity();

        siteUserRepository.save(siteUser);

        SiteUserInfoEvent message = SiteUserInfoEvent.fromEntity("Create", siteUser);
        kafkaMessageProducer.send(SiteUserInfoEvent.Topic, message);
    }

    @Transactional
    public TokenDto.AccessRefreshToken login(SiteUserLoginDto loginDto) {
        SiteUser user = siteUserRepository.findByUserId(loginDto.getUserId());
        if (user == null) {
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }

        if( !SecureHashUtils.matches(loginDto.getPassword(), user.getUserPwd())){
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }

        // AccessToken과 RefreshToken 생성
        TokenDto.AccessRefreshToken tokens = tokenGenerator.generateAccessRefreshToken(loginDto.getUserId(), "WEB");
        
        // RefreshToken 만료 시간 계산 (초 단위를 LocalDateTime으로 변환)
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(tokens.getRefresh().getExpiresIn());
        
        // DB에 RefreshToken 저장 (변경 감지를 위해 명시적으로 저장)
        user.updateRefreshToken(tokens.getRefresh().getToken(), refreshTokenExpiresAt);
        SiteUser savedUser = siteUserRepository.save(user);
        
        log.info("사용자 로그인 성공: {}, RefreshToken 저장 완료, modDate: {}", 
                loginDto.getUserId(), savedUser.getModDate());
        
        return tokens;
    }

    @Transactional
    public TokenDto.AccessToken refresh(SiteUserRefreshDto refreshDto) {
        // JWT 토큰 검증
        String userId = tokenGenerator.validateJwtToken(refreshDto.getToken());
        if (userId == null) {
            throw new BadParameter("토큰이 유효하지 않습니다.");
        }
        
        // 사용자 조회
        SiteUser user = siteUserRepository.findByUserId(userId);
        if (user == null) {
            throw new NotFound("사용자를 찾을 수 없습니다.");
        }
        
        // DB에 저장된 RefreshToken과 비교
        if (!refreshDto.getToken().equals(user.getRefreshToken())) {
            log.warn("저장된 RefreshToken과 불일치: {}", userId);
            throw new BadParameter("유효하지 않은 RefreshToken입니다.");
        }
        
        // RefreshToken 만료 여부 확인
        if (!user.isRefreshTokenValid()) {
            log.warn("RefreshToken 만료: {}", userId);
            user.clearRefreshToken();
            SiteUser savedUser = siteUserRepository.save(user);
            log.info("RefreshToken 삭제 완료, modDate: {}", savedUser.getModDate());
            throw new BadParameter("RefreshToken이 만료되었습니다.");
        }
        
        // 새로운 AccessToken 생성
        TokenDto.AccessToken newAccessToken = tokenGenerator.generateAccessToken(userId, "WEB");
        
        log.info("토큰 갱신 성공: {}", userId);
        
        return newAccessToken;
    }

    @Transactional(readOnly = true)
    public SiteUserInfoDto userInfo(String userId) {
        SiteUser user = siteUserRepository.findByUserId(userId);
        if (user == null) {
            throw new NotFound("사용자를 찾을 수 없습니다.");
        }

        return SiteUserInfoDto.fromEntity(user);
    }

    @Transactional
    public ActionAndId registerUserAndNotify(SiteUserRegisterDto registerDto) {
        SiteUser siteUser = registerDto.toEntity();

        siteUserRepository.save(siteUser);

        return ActionAndId.of("Create", siteUser.getId());
    }

    /**
     * 사용자 로그아웃 처리
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(String userId) {
        SiteUser user = siteUserRepository.findByUserId(userId);
        if (user != null) {
            user.clearRefreshToken();
            SiteUser savedUser = siteUserRepository.save(user);
            log.info("사용자 로그아웃 완료: {}, modDate: {}", userId, savedUser.getModDate());
        }
    }
}