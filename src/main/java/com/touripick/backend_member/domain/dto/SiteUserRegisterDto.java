package com.touripick.backend_member.domain.dto;

import com.touripick.backend_member.domain.SiteUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Getter
@Setter
public class SiteUserRegisterDto {
    @NotBlank(message = "아이디를 입력하세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String userPwd;

    @NotBlank(message = "이름을 입력하세요.")
    private String userName;

    @NotBlank(message = "이메일을 입력하세요.")
    private String userEmail;

    @NotBlank(message = "전화번호를 입력하세요.")
    private String phone;

    public SiteUser toEntity() {
        SiteUser siteUser = new SiteUser();
        siteUser.setUserId(this.userId);
        siteUser.setPhone(this.phone);
        // password를 SHA256 해시 값으로 변환
        String hashedPassword = hashSha256(this.userPwd);
        siteUser.setUserPwd(hashedPassword);
        siteUser.setUserName(this.userName);
        siteUser.setUserEmail(this.userEmail);
        siteUser.setRegDate(LocalDateTime.now());
        return siteUser;
    }

    // SHA256 해싱 메서드
    private String hashSha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            // 바이트 배열을 16진수 문자열로 변환 (또는 Base64로 변환)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            // 또는 Base64 인코딩을 사용할 경우:
            // return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 알고리즘을 찾을 수 없는 경우 (거의 발생하지 않음)
            e.printStackTrace();
            return null; // 또는 예외를 다시 던지거나 적절한 오류 처리를 합니다.
        }
    }
}