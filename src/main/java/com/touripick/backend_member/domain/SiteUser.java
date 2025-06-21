package com.touripick.backend_member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "site_user")
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

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    @Setter @Getter
    private LocalDateTime regDate;
}
