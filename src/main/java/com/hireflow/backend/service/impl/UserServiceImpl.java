package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.UserProfileResponse;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.entity.UserRole;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.repository.UserRoleRepository;
import com.hireflow.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService implementasyonu
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserProfileResponse getUserProfile(String email) {
        System.out.println("[UserService] Getting profile for email: " + email);
        
        // Kullanıcıyı email ile bul
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Reflection ile User field'larına eriş (Lombok getter sorunu)
        UUID userId = getFieldValue(user, "userId", UUID.class);
        String userName = getFieldValue(user, "name", String.class);
        String userSurname = getFieldValue(user, "surname", String.class);
        String userEmail = getFieldValue(user, "email", String.class);

        System.out.println("[UserService] User found: " + userId + ", " + userName + " " + userSurname);

        // Kullanıcının aktif rollerini çek
        List<UserRole> userRoles = userRoleRepository.findByUser_UserId(userId);
        System.out.println("[UserService] Found " + userRoles.size() + " roles");

        // Aktif rolleri filtrele ve SHRT_CODE'ları al
        List<String> roles = userRoles.stream()
                .filter(userRole -> {
                    Short isActv = getFieldValue(userRole, "isActv", Short.class);
                    return isActv != null && isActv == 1;
                })
                .map(userRole -> {
                    GeneralType roleType = getFieldValue(userRole, "roleType", GeneralType.class);
                    if (roleType != null) {
                        String shrtCode = getFieldValue(roleType, "shrtCode", String.class);
                        System.out.println("[UserService] Role found: " + shrtCode);
                        return shrtCode;
                    }
                    return null;
                })
                .filter(role -> role != null)
                .collect(Collectors.toList());

        // Primary role'ü belirle (ilk rol)
        String primaryRole = roles.isEmpty() ? null : roles.get(0);
        System.out.println("[UserService] Primary role: " + primaryRole);

        return new UserProfileResponse(
                userId,
                userEmail,
                userName,
                userSurname,
                roles,
                primaryRole
        );
    }

    /**
     * Reflection ile private field'a eriş
     */
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> type) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (Exception e) {
            System.err.println("[UserService] Failed to get field " + fieldName + ": " + e.getMessage());
            return null;
        }
    }
}
