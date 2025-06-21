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
//import com.touripick.backend_member.event.producer.KafkaMessageProducer;
import com.touripick.backend_member.secret.hash.SecureHashUtils;
import com.touripick.backend_member.secret.jwt.TokenGenerator;
import com.touripick.backend_member.secret.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 사이트 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final TokenGenerator tokenGenerator;
//    private final KafkaMessageProducer kafkaMessageProducer;

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
        // TODO: 비밀번호 암호화 로직 추가 필요 (SecureHashUtils 사용 등)
        SiteUser siteUser = registerDto.toEntity();

        siteUserRepository.save(siteUser);

        SiteUserInfoEvent message = SiteUserInfoEvent.fromEntity("Create", siteUser);
//        kafkaMessageProducer.send(SiteUserInfoEvent.Topic, message);
    }

    @Transactional(readOnly = true)
    public TokenDto.AccessRefreshToken login(SiteUserLoginDto loginDto) {
        SiteUser user = siteUserRepository.findByUserId(loginDto.getUserId());
        if (user == null) {
            throw new BadParameter("아이디 확인하세요.");
        }

        if( !SecureHashUtils.matches(loginDto.getPassword(), user.getUserPwd())){
            throw new BadParameter("비밀번호를 확인하세요.");
        }
        return tokenGenerator.generateAccessRefreshToken(loginDto.getUserId(), "WEB");
    }

    @Transactional(readOnly = true)
    public TokenDto.AccessToken refresh(SiteUserRefreshDto refreshDto) {
        String userId = tokenGenerator.validateJwtToken(refreshDto.getToken());
        if (userId == null) {
            throw new BadParameter("토큰이 유효하지 않습니다.");
        }
        SiteUser user = siteUserRepository.findByUserId(userId);
        if (user == null) {
            throw new NotFound("사용자를 찾을 수 없습니다.");
        }
        return tokenGenerator.generateAccessToken(userId, "WEB");
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
}