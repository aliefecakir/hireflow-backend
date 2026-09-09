package com.hireflow.backend.repository;

import com.hireflow.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** USER_ROLE; JWT authority üretimi. */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    /**
     * Kullanıcı ID'sine göre tüm rolleri bulur
     * @param userId Kullanıcının UUID'si
     * @return List<UserRole>
     */
    List<UserRole> findByUser_UserId(UUID userId);

    @Query("""
            select ur from UserRole ur
            join fetch ur.user u
            join fetch ur.roleType
            where ur.isActv = 1
            order by u.surname asc, u.name asc, u.email asc
            """)
    List<UserRole> findActiveWithUserAndRole();
}
