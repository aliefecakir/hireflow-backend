package com.hireflow.backend.repository;

import com.hireflow.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    /**
     * Kullanıcı ID'sine göre tüm rolleri bulur
     * @param userId Kullanıcının UUID'si
     * @return List<UserRole>
     */
    List<UserRole> findByUser_UserId(UUID userId);
}
