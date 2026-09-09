package com.hireflow.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/** Akademi RBAC: ACADEMY_MNGR / ADMIN yazabilir, ACADEMY_VISITOR salt okur. */
public final class AcademyRoles {

    public static final String READ = "hasAnyRole('ACADEMY_MNGR', 'ADMIN', 'ACADEMY_VISITOR')";
    public static final String WRITE = "hasAnyRole('ACADEMY_MNGR', 'ADMIN')";
    public static final String ADMIN = "hasRole('ADMIN')";

    private static final Set<String> READ_AUTHORITIES = Set.of(
            "ROLE_ACADEMY_MNGR",
            "ROLE_ADMIN",
            "ROLE_ACADEMY_VISITOR"
    );

    private AcademyRoles() {
    }

    public static boolean hasReadAccess(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(READ_AUTHORITIES::contains);
    }
}
