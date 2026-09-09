package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.ApplicationCreateRequest;
import com.hireflow.backend.dto.ApplicationResponse;
import com.hireflow.backend.entity.Application;
import com.hireflow.backend.entity.GnlSt;
import com.hireflow.backend.entity.Post;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.ApplicationRepository;
import com.hireflow.backend.repository.GnlStRepository;
import com.hireflow.backend.repository.PostRepository;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.service.ApplicationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/** İlan başvurusu: aktif POST kontrolü, çift başvuru, APP/WAIT statü, legacy kod map. */
@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    private static final String APP_ENTITY_CODE = "APP";
    private static final String POST_ENTITY_CODE = "POST";
    private static final String ACTIVE_POST_CODE = "ACTV";
    private static final String DEFAULT_APP_STATUS = "WAIT";
    private static final String LEGACY_WAIT_STATUS = "DISPATCHED"; // eski kod uyumu

    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GnlStRepository gnlStRepository;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            GnlStRepository gnlStRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.gnlStRepository = gnlStRepository;
    }

    @Override
    @Transactional
    public ApplicationResponse applyToPost(UUID currentUserId, ApplicationCreateRequest request) {
        UUID postId = request.postId();
        if (postId == null) {
            throw new BadRequestException("İlan bilgisi zorunludur.");
        }

        User candidate = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BadRequestException("Kullanıcı bulunamadı."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("İlan bulunamadı."));

        if (!isActivePost(post)) {
            throw new BadRequestException("Yalnızca aktif ilanlara başvurulabilir.");
        }

        if (applicationRepository.existsByPost_PostIdAndCandidate_UserId(postId, currentUserId)) {
            throw new BadRequestException("Bu ilana zaten başvurdunuz");
        }

        GnlSt waitStatus = resolveDefaultAppStatus(); // APP/WAIT (eski: DISPATCHED)

        Application application = new Application();
        application.setPost(post);
        application.setCandidate(candidate);
        application.setStatus(waitStatus);
        application.setCuser(currentUserId);
        application.setUuser(currentUserId);

        try {
            return toResponse(applicationRepository.save(application));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Bu ilana zaten başvurdunuz");
        }
    }

    @Override
    public List<ApplicationResponse> getMyApplications(UUID currentUserId) {
        return applicationRepository.findAllByCandidate_UserIdOrderByCdateDesc(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAllByOrderByCdateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByPost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("İlan bulunamadı.");
        }
        return applicationRepository.findAllByPost_PostIdOrderByCdateDesc(postId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(UUID appId, String statusCode, UUID hrUserId) {
        Application application = applicationRepository.findDetailedById(appId)
                .orElseThrow(() -> new NoSuchElementException("Başvuru bulunamadı."));

        GnlSt status = resolveAppStatus(statusCode);
        application.setStatus(status);
        application.setUuser(hrUserId);

        return toResponse(applicationRepository.save(application));
    }

    private boolean isActivePost(Post post) {
        if (post.getStId() == null) {
            return false;
        }
        return gnlStRepository.findById(post.getStId())
                .filter(status -> POST_ENTITY_CODE.equals(status.getEntCodeName()))
                .map(GnlSt::getShrtCode)
                .filter(ACTIVE_POST_CODE::equalsIgnoreCase)
                .isPresent();
    }

    private GnlSt resolveDefaultAppStatus() {
        return gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, DEFAULT_APP_STATUS)
                .or(() -> gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, LEGACY_WAIT_STATUS))
                .orElseThrow(() -> new BadRequestException(
                        "Başvuru için varsayılan statü bulunamadı (APP / WAIT)."));
    }

    private GnlSt resolveAppStatus(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            throw new BadRequestException("Statü kodu zorunludur.");
        }
        String code = statusCode.trim().toUpperCase();
        return gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, code)
                .or(() -> switch (code) { // yeni kod -> eski GNL_ST kodu
                    case "WAIT" -> gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, "DISPATCHED");
                    case "REVIEW" -> gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, "PROCESS");
                    case "APPR" -> gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, "APPRV");
                    case "REJ" -> gnlStRepository.findByEntCodeNameAndShrtCode(APP_ENTITY_CODE, "RJCTD");
                    default -> java.util.Optional.empty();
                })
                .orElseThrow(() -> new BadRequestException(
                        "Başvuru statüsü bulunamadı: " + code));
    }

    private ApplicationResponse toResponse(Application application) {
        Post post = application.getPost();
        User candidate = application.getCandidate();
        GnlSt status = application.getStatus();

        ApplicationResponse.Status statusDto = status == null
                ? null
                : new ApplicationResponse.Status(status.getShrtCode(), status.getName(), status.getDescr());

        return new ApplicationResponse(
                application.getAppId(),
                post == null ? null : post.getPostId(),
                post == null ? null : post.getTitle(),
                post == null ? null : post.getDescr(),
                post == null ? null : post.getReqTech(),
                post == null ? null : post.getReqDept(),
                userField(candidate, "userId", UUID.class),
                userField(candidate, "name", String.class),
                userField(candidate, "surname", String.class),
                userField(candidate, "email", String.class),
                statusDto,
                application.getCdate()
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T userField(User user, String fieldName, Class<T> type) {
        if (user == null) {
            return null;
        }
        Class<?> typeToScan = user.getClass();
        while (typeToScan != null && typeToScan != Object.class) {
            try {
                java.lang.reflect.Field field = typeToScan.getDeclaredField(fieldName); // Lombok getter proxy
                field.setAccessible(true);
                return type.cast(field.get(user));
            } catch (NoSuchFieldException ignored) {
                typeToScan = typeToScan.getSuperclass();
            } catch (Exception ex) {
                throw new IllegalStateException("USER." + fieldName + " okunamadı", ex);
            }
        }
        return null;
    }
}
