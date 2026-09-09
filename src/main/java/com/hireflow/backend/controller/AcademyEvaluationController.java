package com.hireflow.backend.controller;

import com.hireflow.backend.dto.AcademyAppDetailsResponse;
import com.hireflow.backend.dto.AcademyAppStatusResponse;
import com.hireflow.backend.dto.AcademyEvaluateResponse;
import com.hireflow.backend.dto.EvaluateAcademyAppRequest;
import com.hireflow.backend.dto.FormApplicationResponse;
import com.hireflow.backend.dto.ManualScoreResponse;
import com.hireflow.backend.dto.UpdateAcademyAppStatusRequest;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.service.AcademyAppService;
import com.hireflow.backend.service.AcademyEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Akademi yöneticisinin başvuru detay, puanlama ve durum güncelleme API'si. */
@RestController
@RequestMapping("/api/academy/applications")
@PreAuthorize("hasRole('ACADEMY_MNGR')")
public class AcademyEvaluationController {

    private final AcademyEvaluationService academyEvaluationService;
    private final AcademyAppService academyAppService;
    private final UserRepository userRepository;

    public AcademyEvaluationController(
            AcademyEvaluationService academyEvaluationService,
            AcademyAppService academyAppService,
            UserRepository userRepository
    ) {
        this.academyEvaluationService = academyEvaluationService;
        this.academyAppService = academyAppService;
        this.userRepository = userRepository;
    }

    /** GNL_ST'teki akademi başvuru durumlarını listeler. */
    @GetMapping("/statuses")
    public ResponseEntity<List<AcademyAppStatusResponse>> getApplicationStatuses() {
        return ResponseEntity.ok(academyAppService.getApplicationStatuses());
    }

    /** Aday cevapları + mülakat kriterlerini birlikte döner. */
    @GetMapping("/{appId}/details")
    public ResponseEntity<AcademyAppDetailsResponse> getApplicationDetails(
            @PathVariable("appId") Long appId
    ) {
        return ResponseEntity.ok(academyEvaluationService.getApplicationDetails(appId));
    }

    /** Mülakat şıklarını kaydeder, manuel puanları uygular, total/interview skorunu yazar. */
    @PostMapping("/{appId}/evaluate")
    public ResponseEntity<AcademyEvaluateResponse> evaluateApplication(
            @PathVariable("appId") Long appId,
            @Valid @RequestBody EvaluateAcademyAppRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(academyEvaluationService.evaluateApplication(appId, request, currentUserId(jwt)));
    }

    /** Tek bir açık uçlu / "diğer" cevabına manuel puan basar. */
    @PostMapping("/{appId}/manual-score")
    public ResponseEntity<ManualScoreResponse> saveManualScore(
            @PathVariable("appId") Long appId,
            @Valid @RequestBody EvaluateAcademyAppRequest.ManualScoreRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(academyEvaluationService.saveManualScore(appId, request, currentUserId(jwt)));
    }

    /** Başvuru ST_ID ve status açıklamasını günceller. */
    @PutMapping("/{appId}/status")
    public ResponseEntity<FormApplicationResponse> updateApplicationStatus(
            @PathVariable("appId") Long appId,
            @Valid @RequestBody UpdateAcademyAppStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(academyAppService.updateApplicationStatus(appId, request, currentUserId(jwt)));
    }

    /** JWT email (yoksa sub) ile USER tablosundan değerlendiren kullanıcıyı çözer. */
    private UUID currentUserId(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        }

        if (jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
