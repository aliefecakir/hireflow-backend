package com.hireflow.backend.security;

import com.hireflow.backend.entity.GeneralType;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.entity.UserRole;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.repository.UserRoleRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JWT token'ı Spring Security Authentication'a dönüştürür
 * Kullanıcının veritabanındaki aktif rollerini Spring Security yetkilerine çevirir
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public JwtAuthConverter(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // JWT'den email claim'ini al (Supabase JWT'sinde email claim'i var)
        String email = jwt.getClaimAsString("email");
        String subject = jwt.getSubject();
        
        System.out.println("=== JWT AUTH CONVERTER ===");
        System.out.println("JWT Email: " + email);
        System.out.println("JWT Subject: " + subject);
        
        // Email yoksa sub (subject/UUID) kullan
        if (email == null || email.isEmpty()) {
            email = subject;
        }

        // Kullanıcının yetkilerini çöz
        Collection<GrantedAuthority> authorities = extractAuthorities(email);
        
        System.out.println("Extracted Authorities: " + authorities);
        System.out.println("==========================");

        // JwtAuthenticationToken oluştur ve döndür
        return new JwtAuthenticationToken(jwt, authorities, email);
    }

    /**
     * Kullanıcının email adresine göre veritabanından aktif rollerini çeker
     * ve Spring Security yetkilerine dönüştürür
     * 
     * @param email Kullanıcının email adresi
     * @return GrantedAuthority listesi (ROLE_ önekli)
     */
    private Collection<GrantedAuthority> extractAuthorities(String email) {
        System.out.println("[extractAuthorities] Email: " + email);
        
        if (email == null || email.isEmpty()) {
            System.out.println("[extractAuthorities] Email null/empty, returning empty authorities");
            return Collections.emptyList();
        }

        // Kullanıcıyı email ile bul
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        System.out.println("[extractAuthorities] User found: " + userOptional.isPresent());
        
        if (userOptional.isEmpty()) {
            // Kullanıcı veritabanında yok, boş yetki listesi döndür
            // (Exception atmıyoruz, akışı kesmesin)
            System.out.println("[extractAuthorities] User not found in DB, returning empty authorities");
            return Collections.emptyList();
        }

        User user = userOptional.get();

        // Kullanıcının aktif rollerini çek (IS_ACTV = 1)
        // Kullanıcı ID'si UUID tipinde, UserRepository'den gelen user nesnesinden alınıyor
        java.util.UUID userId = null;
        try {
            // Reflection ile userId field'ına eriş (Lombok getter sorunu için)
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userId = (java.util.UUID) userIdField.get(user);
            System.out.println("[extractAuthorities] User ID: " + userId);
        } catch (Exception e) {
            System.out.println("[extractAuthorities] Failed to get userId: " + e.getMessage());
            return Collections.emptyList();
        }
        
        if (userId == null) {
            System.out.println("[extractAuthorities] User ID is null, returning empty authorities");
            return Collections.emptyList();
        }
        
        List<UserRole> userRoles = userRoleRepository.findByUser_UserId(userId);
        System.out.println("[extractAuthorities] Found " + userRoles.size() + " roles for user");

        // Aktif rolleri filtrele ve ROLE_ önekiyle authorities'e dönüştür
        return userRoles.stream()
                .filter(userRole -> {
                    try {
                        java.lang.reflect.Field isActvField = UserRole.class.getDeclaredField("isActv");
                        isActvField.setAccessible(true);
                        Short isActv = (Short) isActvField.get(userRole);
                        return isActv != null && isActv == 1;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(userRole -> {
                    try {
                        java.lang.reflect.Field roleTypeField = UserRole.class.getDeclaredField("roleType");
                        roleTypeField.setAccessible(true);
                        return roleTypeField.get(userRole) != null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(userRole -> {
                    try {
                        java.lang.reflect.Field roleTypeField = UserRole.class.getDeclaredField("roleType");
                        roleTypeField.setAccessible(true);
                        GeneralType roleType = (GeneralType) roleTypeField.get(userRole);
                        
                        java.lang.reflect.Field shrtCodeField = GeneralType.class.getDeclaredField("shrtCode");
                        shrtCodeField.setAccessible(true);
                        String shrtCode = (String) shrtCodeField.get(roleType);
                        
                        System.out.println("[extractAuthorities] Mapping role: ROLE_" + shrtCode);
                        return new SimpleGrantedAuthority("ROLE_" + shrtCode);
                    } catch (Exception e) {
                        System.out.println("[extractAuthorities] Failed to map role: " + e.getMessage());
                        return new SimpleGrantedAuthority("ROLE_UNKNOWN");
                    }
                })
                .filter(authority -> {
                    boolean keep = !authority.getAuthority().equals("ROLE_UNKNOWN");
                    if (!keep) {
                        System.out.println("[extractAuthorities] Filtered out ROLE_UNKNOWN");
                    }
                    return keep;
                })
                .collect(Collectors.toList());
    }
}
