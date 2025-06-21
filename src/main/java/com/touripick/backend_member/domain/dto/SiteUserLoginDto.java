package com.touripick.backend_member.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteUserLoginDto {
    private String userId;
    private String password;
}
