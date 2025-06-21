package com.touripick.backend_member.api.open;


import com.touripick.backend_member.common.dto.ApiResponseDto;
//import com.touripick.backend_member.remote.alim.RemoteAlimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
//    private final RemoteAlimService remoteAlimService;

    @GetMapping(value = "/test")
    public String test() {
        return "Hello Kubernetes";

    }

//    @GetMapping(value = "/hello")
//    public ApiResponseDto<String> hello() {
//        String remoteMessage = remoteAlimService.hello().getData();
//        String userResponse = "웰컴 투 유저 서비스. 리모트 알림 메시지= " + remoteMessage;
//
//        return ApiResponseDto.createOk(userResponse);
//    }
}
