package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreatePostRequest;
import com.hireflow.backend.dto.PostResponse;
import com.hireflow.backend.dto.UpdatePostRequest;
import com.hireflow.backend.dto.UpdatePostStatusRequest;
import com.hireflow.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * İlan (job posting) işlemleri için REST controller
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private static final String ROLE_HR = "ROLE_HR";
    private static final String ACTIVE_STATUS_CODE = "ACTV";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Aday ekranı: yalnızca aktif (ACTV) ilanları listeler.
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getActivePosts(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID currentUserId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(postService.getActivePosts(currentUserId));
    }

    /**
     * İK yönetim ekranı: tüm ilanları (veya status filtresine göre) listeler.
     */
    @GetMapping("/manage")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<PostResponse>> getAllPostsForAdmin(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(postService.getAllPostsForAdmin(status));
    }

    /**
     * İlan detayı. Adaylar yalnızca ACTV ilanları görebilir; HR tüm statüleri görebilir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable("id") UUID id,
            Authentication authentication
    ) {
        PostResponse post = postService.getPostById(id);
        if (!hasHrRole(authentication) && !isActive(post)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        return ResponseEntity.ok(post);
    }

    /**
     * Yeni ilan oluşturur. Sadece HR erişebilir.
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        PostResponse created = postService.createPost(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * İlanı günceller. Sadece HR erişebilir.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(postService.updatePost(id, request, currentUserId));
    }

    /**
     * İlan statüsünü değiştirir (ACTV / PASS / DRFT). Sadece HR erişebilir.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PostResponse> updatePostStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdatePostStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(postService.updatePostStatus(id, request.statusCode(), currentUserId));
    }

    /**
     * İlanı siler. Sadece HR erişebilir.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deletePost(@PathVariable("id") UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /** JWT subject'ten oturum kullanıcısını çözer. */
    private boolean hasHrRole(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_HR::equals);
    }

    /** Statü kısa kodu ACTV mi. */
    private boolean isActive(PostResponse post) {
        return post.status() != null && ACTIVE_STATUS_CODE.equals(post.status().shrtCode());
    }
}
