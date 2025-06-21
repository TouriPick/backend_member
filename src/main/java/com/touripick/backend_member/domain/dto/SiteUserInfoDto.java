package com.touripick.backend_member.domain.dto;

import com.touripick.backend_member.domain.SiteUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteUserInfoDto {
    private String userId;

    private String phoneNumber;

    public static SiteUserInfoDto fromEntity(SiteUser siteUser) {
        SiteUserInfoDto dto = new SiteUserInfoDto();

        dto.userId = siteUser.getUserId();
        dto.phoneNumber = siteUser.getPhone();

        return dto;
    }
}