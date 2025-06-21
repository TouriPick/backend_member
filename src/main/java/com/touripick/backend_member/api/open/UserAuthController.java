package com.touripick.backend_member.api.open;

import com.touripick.backend_member.common.dto.ApiResponseDto;
import com.touripick.backend_member.domain.dto.SiteUserLoginDto;
import com.touripick.backend_member.domain.dto.SiteUserRefreshDto;
import com.touripick.backend_member.domain.dto.SiteUserRegisterDto;
import com.touripick.backend_member.secret.jwt.dto.TokenDto;
import com.touripick.backend_member.service.SiteUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 공개 API 중 사용자 인증(회원가입, 로그인 등) 관련 기능을 제공하는 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/user/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserAuthController {
    private final SiteUserService siteUserService;

    /**
     * 사용자 회원가입을 처리합니다.
     * 요청 본문으로 {@link SiteUserRegisterDto}를 받아 유효성 검사를 수행합니다.
     *
     * @param registerDto 회원가입 정보를 담은 DTO
     * @return 작업 성공 여부를 담은 {@link ApiResponseDto} (데이터는 null)
     */
    @PostMapping(value = "/register")
    public ApiResponseDto<String> register(@RequestBody @Valid SiteUserRegisterDto registerDto) {
        siteUserService.registerUser(registerDto);
        return ApiResponseDto.defaultOk();
    }

    @PostMapping(value = "/login")
    public ApiResponseDto<TokenDto.AccessRefreshToken> login(@RequestBody @Valid SiteUserLoginDto loginDto) {
        TokenDto.AccessRefreshToken token = siteUserService.login(loginDto);
        return ApiResponseDto.createOk(token);
    }

    @PostMapping(value = "/refresh")
    public ApiResponseDto<TokenDto.AccessToken> refresh(@RequestBody @Valid SiteUserRefreshDto refreshDto) {
        TokenDto.AccessToken token = siteUserService.refresh(refreshDto);
        return ApiResponseDto.createOk(token);
    }

    @GetMapping(value = "/test")
    public ApiResponseDto<String> test() {
        return ApiResponseDto.createOk("카나리 버전입니다.");
    }
}
