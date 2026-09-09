package com.hireflow.backend.repository;

import com.hireflow.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/** USER; JWT email eşlemesi. */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Email adresine göre kullanıcı bulur
     * @param email Kullanıcının email adresi
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);
}
