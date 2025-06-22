package com.touripick.backend_member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "site_user")
@EntityListeners(AuditingEntityListener.class)
public class SiteUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 20)
    @Getter @Setter
    private String userId;

    @Column(name = "user_pwd", nullable = false, columnDefinition = "TEXT")
    @Getter @Setter
    private String userPwd;

    @Column(name = "user_name", nullable = false, length = 20)
    @Getter @Setter
    private String userName;

    @Column(name = "user_email", nullable = false, unique = true, length = 225)
    @Getter @Setter
    private String userEmail;

    @Column(name = "phone", nullable = false, length = 13)
    @Setter @Getter
    private String phone;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    @Getter @Setter
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    @Getter @Setter
    private LocalDateTime refreshTokenExpiresAt;

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    @Setter @Getter
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "mod_date")
    @Setter @Getter
    private LocalDateTime modDate;

    // RefreshToken 관련 메서드들
    public void updateRefreshToken(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = expiresAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiresAt = null;
    }

    public boolean isRefreshTokenValid() {
        return this.refreshToken != null && 
               this.refreshTokenExpiresAt != null && 
               this.refreshTokenExpiresAt.isAfter(LocalDateTime.now());
    }
}
