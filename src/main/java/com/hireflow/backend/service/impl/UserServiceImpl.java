package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.SystemRoleResponse;
import com.hireflow.backend.dto.UpdateUserRoleRequest;
import com.hireflow.backend.dto.UserProfileResponse;
import com.hireflow.backend.dto.UserRoleRowResponse;
import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.entity.UserRole;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.GeneralTypeRepository;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.repository.UserRoleRepository;
import com.hireflow.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService implementasyonu
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Short ACTIVE = 1;
    private static final Short INACTIVE = 0;
    private static final String ADMIN_CODE = "ADMIN";
    private static final Set<String> KNOWN_ROLE_CODES = Set.of(
            "CAND",
            "HR",
            "MNGR",
            "ACADEMY_MNGR",
            "ADMIN",
            "ACADEMY_VISITOR"
    );

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final GeneralTypeRepository generalTypeRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            GeneralTypeRepository generalTypeRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.generalTypeRepository = generalTypeRepository;
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

    @Override
    public List<UserRoleRowResponse> listUserRoles() {
        Map<UUID, UserRoleRowResponse> uniqueUsers = new LinkedHashMap<>();
        for (UserRole userRole : userRoleRepository.findActiveWithUserAndRole()) {
            UserRoleRowResponse row = toRow(userRole);
            if (row.userId() != null) {
                uniqueUsers.putIfAbsent(row.userId(), row);
            }
        }
        List<UserRoleRowResponse> rows = new ArrayList<>(uniqueUsers.values());
        rows.sort(Comparator
                .comparing(UserRoleRowResponse::lastName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(UserRoleRowResponse::firstName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(UserRoleRowResponse::email, Comparator.nullsLast(String::compareToIgnoreCase)));
        return rows;
    }

    @Override
    public List<SystemRoleResponse> listActiveRoles() {
        return generalTypeRepository.findByIsActvOrderByNameAsc(ACTIVE).stream()
                .filter(this::isSystemRole)
                .map(this::toRoleResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserRoleRowResponse updateUserRole(UUID userId, UpdateUserRoleRequest request, UUID actorUserId) {
        if (userId == null) {
            throw new BadRequestException("Kullanıcı seçilmedi.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Kullanıcı bulunamadı."));
        GeneralType nextRole = resolveRole(request);
        if (!isActive(nextRole.getIsActv()) || !isSystemRole(nextRole)) {
            throw new BadRequestException("Seçilen rol aktif bir sistem rolü değil.");
        }

        List<UserRole> existing = userRoleRepository.findByUser_UserId(userId);
        LocalDateTime now = LocalDateTime.now();
        UserRole matching = existing.stream()
                .filter(row -> sameRole(row.getRoleType(), nextRole))
                .findFirst()
                .orElse(null);

        UserRole target;
        if (matching != null) {
            matching.setIsActv(ACTIVE);
            matching.setIsAdmin(isAdminRole(nextRole) ? ACTIVE : INACTIVE);
            matching.setUdate(now);
            matching.setUuser(actorUserId);
            target = matching;
        } else {
            UserRole firstActive = existing.stream()
                    .filter(row -> isActive(row.getIsActv()))
                    .findFirst()
                    .orElse(null);
            if (firstActive != null) {
                firstActive.setRoleType(nextRole);
                firstActive.setIsActv(ACTIVE);
                firstActive.setIsAdmin(isAdminRole(nextRole) ? ACTIVE : INACTIVE);
                firstActive.setUdate(now);
                firstActive.setUuser(actorUserId);
                target = firstActive;
            } else {
                UserRole created = new UserRole();
                created.setUserRoleId(UUID.randomUUID());
                created.setUser(user);
                created.setRoleType(nextRole);
                created.setIsActv(ACTIVE);
                created.setIsAdmin(isAdminRole(nextRole) ? ACTIVE : INACTIVE);
                created.setCdate(now);
                created.setCuser(actorUserId);
                target = userRoleRepository.save(created);
            }
        }

        for (UserRole row : existing) {
            if (row == target) {
                continue;
            }
            if (isActive(row.getIsActv())) {
                row.setIsActv(INACTIVE);
                row.setUdate(now);
                row.setUuser(actorUserId);
            }
        }

        userRoleRepository.saveAll(existing.contains(target) ? existing : concat(existing, target));
        return toRow(target);
    }

    private List<UserRole> concat(List<UserRole> existing, UserRole extra) {
        List<UserRole> rows = new ArrayList<>(existing);
        rows.add(extra);
        return rows;
    }

    private GeneralType resolveRole(UpdateUserRoleRequest request) {
        if (request == null) {
            throw new BadRequestException("Rol seçilmedi.");
        }
        if (request.roleId() != null) {
            return generalTypeRepository.findById(request.roleId())
                    .orElseThrow(() -> new NoSuchElementException("Rol bulunamadı."));
        }
        String shrtCode = request.shrtCode() == null ? "" : request.shrtCode().trim();
        if (shrtCode.isEmpty()) {
            throw new BadRequestException("Rol seçilmedi.");
        }
        return generalTypeRepository.findByShrtCodeIgnoreCase(shrtCode)
                .or(() -> generalTypeRepository.findByShrtCode(shrtCode))
                .orElseThrow(() -> new NoSuchElementException("Rol bulunamadı."));
    }

    private boolean isSystemRole(GeneralType type) {
        if (type == null) {
            return false;
        }
        String code = normalize(type.getShrtCode());
        String ent = normalize(type.getEntCodeName());
        if (KNOWN_ROLE_CODES.contains(code)) {
            return true;
        }
        return ent.contains("ROLE");
    }

    private boolean sameRole(GeneralType left, GeneralType right) {
        if (left == null || right == null || left.getGnlTpId() == null || right.getGnlTpId() == null) {
            return false;
        }
        return left.getGnlTpId().equals(right.getGnlTpId());
    }

    private boolean isAdminRole(GeneralType type) {
        return ADMIN_CODE.equals(normalize(type == null ? null : type.getShrtCode()));
    }

    private boolean isActive(Short value) {
        return value != null && value == 1;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private SystemRoleResponse toRoleResponse(GeneralType type) {
        return new SystemRoleResponse(
                type.getGnlTpId(),
                type.getName(),
                type.getDescr(),
                type.getShrtCode(),
                type.getIsActv()
        );
    }

    private UserRoleRowResponse toRow(UserRole userRole) {
        User user = userRole.getUser();
        GeneralType role = userRole.getRoleType();
        return new UserRoleRowResponse(
                user == null ? null : user.getUserId(),
                userRole.getUserRoleId(),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getName(),
                user == null ? null : user.getSurname(),
                role == null ? null : role.getGnlTpId(),
                role == null ? null : role.getName(),
                role == null ? null : role.getShrtCode(),
                userRole.getIsActv()
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
