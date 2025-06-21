package com.touripick.backend_member.domain.repository;

import com.touripick.backend_member.domain.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    SiteUser findByUserId(String userId);
}
