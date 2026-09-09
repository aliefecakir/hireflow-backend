package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.ExperienceDto;
import com.hireflow.backend.dto.ExperienceUpdateRequest;
import com.hireflow.backend.dto.LangDto;
import com.hireflow.backend.dto.ProfileDetailResponse;
import com.hireflow.backend.dto.ProfileUpdateRequest;
import com.hireflow.backend.dto.SkillDto;
import com.hireflow.backend.entity.Experience;
import com.hireflow.backend.entity.Lang;
import com.hireflow.backend.entity.Profile;
import com.hireflow.backend.entity.ProfileExpRel;
import com.hireflow.backend.entity.ProfileLangRel;
import com.hireflow.backend.entity.ProfileSkillRel;
import com.hireflow.backend.entity.Skill;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.repository.ExperienceRepository;
import com.hireflow.backend.repository.LangRepository;
import com.hireflow.backend.repository.ProfileExpRelRepository;
import com.hireflow.backend.repository.ProfileLangRelRepository;
import com.hireflow.backend.repository.ProfileRepository;
import com.hireflow.backend.repository.ProfileSkillRelRepository;
import com.hireflow.backend.repository.SkillRepository;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.service.ProfileService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Profil yükle/oluştur, deneyim-skill-dil senkronu, 8 alanlık tamamlanma. */
@Service
public class ProfileServiceImpl implements ProfileService {

    private static final short ACTIVE = 1;
    private static final int COMPLETION_FIELD_COUNT = 8; // 8 alan = %100
    private static final int SKILL_SEARCH_DEFAULT_LIMIT = 10;
    private static final int SKILL_SEARCH_MAX_LIMIT = 50;

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final LangRepository langRepository;
    private final ProfileExpRelRepository profileExpRelRepository;
    private final ProfileSkillRelRepository profileSkillRelRepository;
    private final ProfileLangRelRepository profileLangRelRepository;

    public ProfileServiceImpl(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            ExperienceRepository experienceRepository,
            SkillRepository skillRepository,
            LangRepository langRepository,
            ProfileExpRelRepository profileExpRelRepository,
            ProfileSkillRelRepository profileSkillRelRepository,
            ProfileLangRelRepository profileLangRelRepository
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.experienceRepository = experienceRepository;
        this.skillRepository = skillRepository;
        this.langRepository = langRepository;
        this.profileExpRelRepository = profileExpRelRepository;
        this.profileSkillRelRepository = profileSkillRelRepository;
        this.profileLangRelRepository = profileLangRelRepository;
    }

    @Override
    @Transactional
    public ProfileDetailResponse getMyProfile(UUID currentUserId) {
        return loadOrCreateProfile(currentUserId);
    }

    @Override
    @Transactional
    public ProfileDetailResponse updateMyProfile(UUID currentUserId, ProfileUpdateRequest request) {
        User user = requireUser(currentUserId);
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseGet(() -> createEmptyProfile(user));

        profile.setPhone(blankToNull(request.phone()));
        profile.setDept(blankToNull(request.dept()));
        profile.setEducation(blankToNull(request.education()));
        profile.setPrflPhtUrl(blankToNull(request.prflPhtUrl()));
        profile.setCvUrl(blankToNull(request.cvUrl()));
        profile.setUuser(currentUserId);

        try {
            profile = profileRepository.saveAndFlush(profile);
        } catch (DataIntegrityViolationException ex) {
            throw duplicatePhoneOrRethrow(ex);
        }

        List<Experience> experiences = syncExperiences(profile, request.experiences(), currentUserId);
        List<Skill> skills = syncSkills(profile, request.skillIds(), currentUserId); // replace-all
        List<Lang> languages = syncLanguages(profile, request.langIds(), currentUserId);

        applyCompletion(profile, experiences, skills, languages);
        profile.setUuser(currentUserId);
        profileRepository.save(profile);

        return toDetail(user, profile, experiences, skills, languages);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDetailResponse getProfileByUserId(UUID targetUserId) {
        User user = requireUser(targetUserId);
        Profile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Aday profili bulunamadı."));
        return toDetail(
                user,
                profile,
                loadExperiences(profile.getProfileId()),
                loadSkills(profile.getProfileId()),
                loadLanguages(profile.getProfileId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> getActiveSkills(String query, int limit) {
        if (StringUtils.hasText(query)) {
            int safeLimit = limit <= 0 ? SKILL_SEARCH_DEFAULT_LIMIT : Math.min(limit, SKILL_SEARCH_MAX_LIMIT);
            return skillRepository
                    .findByIsActvAndNameContainingIgnoreCaseOrderByNameAsc(ACTIVE, query.trim())
                    .stream()
                    .limit(safeLimit)
                    .map(this::toSkillDto)
                    .toList();
        }
        int catalogLimit = limit <= 0 ? 500 : Math.min(Math.max(limit, 50), 500);
        return skillRepository.findByIsActvOrderByNameAsc(ACTIVE).stream()
                .limit(catalogLimit)
                .map(this::toSkillDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LangDto> getActiveLanguages() {
        List<Lang> langs = langRepository.findByIsActvOrderByNameAsc(ACTIVE);
        if (langs.isEmpty()) {
            langs = langRepository.findAllByOrderByNameAsc();
        }
        return langs.stream().map(this::toLangDto).toList();
    }

    private ProfileDetailResponse loadOrCreateProfile(UUID userId) {
        User user = requireUser(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(user));
        List<Experience> experiences = loadExperiences(profile.getProfileId());
        List<Skill> skills = loadSkills(profile.getProfileId());
        List<Lang> languages = loadLanguages(profile.getProfileId());
        applyCompletion(profile, experiences, skills, languages);
        profileRepository.save(profile);
        return toDetail(user, profile, experiences, skills, languages);
    }

    private Profile createEmptyProfile(User user) {
        UUID userId = userField(user, "userId", UUID.class);
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setIsCmpltd((short) 0);
        profile.setCuser(userId);
        profile.setUuser(userId);
        return profileRepository.save(profile);
    }

    private User requireUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
    }

    private List<Experience> loadExperiences(UUID profileId) {
        List<ProfileExpRel> rels = profileExpRelRepository.findWithExperienceByProfileId(profileId);
        if (!rels.isEmpty()) {
            return rels.stream()
                    .map(ProfileExpRel::getExperience)
                    .filter(Objects::nonNull)
                    .toList();
        }
        return experienceRepository.findByProfileIdOrderByCdateDesc(profileId);
    }

    private List<Skill> loadSkills(UUID profileId) {
        return profileSkillRelRepository.findWithSkillByProfileId(profileId).stream()
                .map(ProfileSkillRel::getSkill)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Lang> loadLanguages(UUID profileId) {
        return profileLangRelRepository.findWithLangByProfileId(profileId).stream()
                .map(ProfileLangRel::getLang)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Experience> syncExperiences(
            Profile profile,
            List<ExperienceUpdateRequest> incoming,
            UUID currentUserId
    ) {
        List<ExperienceUpdateRequest> items = incoming == null ? List.of() : incoming;
        List<Experience> existingByProfile = experienceRepository.findByProfileIdOrderByCdateDesc(profile.getProfileId());
        List<ProfileExpRel> existingRels = profileExpRelRepository.findWithExperienceByProfileId(profile.getProfileId());

        Map<UUID, Experience> existingById = new LinkedHashMap<>();
        for (Experience exp : existingByProfile) {
            existingById.put(exp.getExperienceId(), exp);
        }
        for (ProfileExpRel rel : existingRels) {
            if (rel.getExperience() != null) {
                existingById.putIfAbsent(rel.getExperience().getExperienceId(), rel.getExperience());
            }
        }

        Set<UUID> keepIds = new HashSet<>();
        List<Experience> saved = new ArrayList<>();

        for (ExperienceUpdateRequest item : items) {
            Experience exp = item.experienceId() != null ? existingById.get(item.experienceId()) : null;
            boolean isNew = exp == null;
            if (isNew) {
                exp = new Experience();
                exp.setCuser(currentUserId);
            }
            exp.setProfileId(profile.getProfileId());
            exp.setCorpName(blankToNull(item.corpName()));
            exp.setPosition(blankToNull(item.position()));
            exp.setDescr(blankToNull(item.descr()));
            exp.setStllWrkg(item.stllWrkg() == null ? 0 : item.stllWrkg());
            exp.setSdate(blankToNull(item.sdate()));
            exp.setEdate(Objects.equals(exp.getStllWrkg(), (short) 1) ? null : blankToNull(item.edate()));
            exp.setUuser(currentUserId);
            exp = experienceRepository.save(exp);
            keepIds.add(exp.getExperienceId());
            saved.add(exp);

            UUID expId = exp.getExperienceId();
            boolean relExists = existingRels.stream()
                    .anyMatch(rel -> rel.getExperience() != null
                            && expId.equals(rel.getExperience().getExperienceId()));
            if (!relExists) {
                ProfileExpRel rel = new ProfileExpRel();
                rel.setProfile(profile);
                rel.setExperience(exp);
                rel.setCuser(currentUserId);
                rel.setUuser(currentUserId);
                profileExpRelRepository.save(rel);
            }
        }

        for (ProfileExpRel rel : existingRels) {
            UUID expId = rel.getExperience() == null ? null : rel.getExperience().getExperienceId();
            if (expId == null || !keepIds.contains(expId)) {
                profileExpRelRepository.delete(rel);
            }
        }

        for (Experience exp : existingById.values()) {
            if (!keepIds.contains(exp.getExperienceId())) {
                experienceRepository.delete(exp);
            }
        }

        saved.sort(Comparator.comparing(Experience::getCdate, Comparator.nullsLast(Comparator.reverseOrder())));
        return saved;
    }

    private List<Skill> syncSkills(Profile profile, List<UUID> skillIds, UUID currentUserId) {
        profileSkillRelRepository.deleteByProfile_ProfileId(profile.getProfileId()); // mevcut skill bağlarını sıfırla
        profileSkillRelRepository.flush();

        if (skillIds == null || skillIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, Skill> skills = skillRepository.findAllById(skillIds).stream()
                .collect(Collectors.toMap(Skill::getSkillId, Function.identity()));

        List<Skill> saved = new ArrayList<>();
        for (UUID skillId : skillIds) {
            Skill skill = skills.get(skillId);
            if (skill == null) {
                continue;
            }
            ProfileSkillRel rel = new ProfileSkillRel();
            rel.setProfile(profile);
            rel.setSkill(skill);
            rel.setCuser(currentUserId);
            rel.setUuser(currentUserId);
            profileSkillRelRepository.save(rel);
            saved.add(skill);
        }
        return saved;
    }

    private List<Lang> syncLanguages(Profile profile, List<UUID> langIds, UUID currentUserId) {
        profileLangRelRepository.deleteByProfile_ProfileId(profile.getProfileId());
        profileLangRelRepository.flush();

        if (langIds == null || langIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, Lang> langs = langRepository.findAllById(langIds).stream()
                .collect(Collectors.toMap(Lang::getLangId, Function.identity()));

        List<Lang> saved = new ArrayList<>();
        for (UUID langId : langIds) {
            Lang lang = langs.get(langId);
            if (lang == null) {
                continue;
            }
            ProfileLangRel rel = new ProfileLangRel();
            rel.setProfile(profile);
            rel.setLang(lang);
            rel.setCuser(currentUserId);
            rel.setUuser(currentUserId);
            profileLangRelRepository.save(rel);
            saved.add(lang);
        }
        return saved;
    }

    private void applyCompletion(
            Profile profile,
            List<Experience> experiences,
            List<Skill> skills,
            List<Lang> languages
    ) {
        int percentage = completionPercentage(profile, experiences, skills, languages);
        profile.setIsCmpltd((short) (percentage == 100 ? 1 : 0)); // 8/8 doluysa 1
    }

    private int completionPercentage(
            Profile profile,
            List<Experience> experiences,
            List<Skill> skills,
            List<Lang> languages
    ) {
        int completed = 0;
        if (hasText(profile.getDept())) completed++;
        if (hasText(profile.getEducation())) completed++;
        if (hasText(profile.getPhone())) completed++;
        if (experiences != null && !experiences.isEmpty()) completed++;
        if (skills != null && !skills.isEmpty()) completed++;
        if (languages != null && !languages.isEmpty()) completed++;
        if (hasText(profile.getPrflPhtUrl())) completed++;
        if (hasText(profile.getCvUrl())) completed++;
        return Math.round((completed * 100f) / COMPLETION_FIELD_COUNT);
    }

    private ProfileDetailResponse toDetail(
            User user,
            Profile profile,
            List<Experience> experiences,
            List<Skill> skills,
            List<Lang> languages
    ) {
        int percentage = completionPercentage(profile, experiences, skills, languages);
        return new ProfileDetailResponse(
                userField(user, "userId", UUID.class),
                userField(user, "name", String.class),
                userField(user, "surname", String.class),
                userField(user, "email", String.class),
                profile.getProfileId(),
                profile.getPhone(),
                profile.getDept(),
                profile.getEducation(),
                profile.getPrflPhtUrl(),
                profile.getCvUrl(),
                profile.getIsCmpltd() == null ? 0 : profile.getIsCmpltd(),
                percentage,
                experiences.stream().map(this::toExperienceDto).toList(),
                skills.stream().map(this::toSkillDto).toList(),
                languages.stream().map(this::toLangDto).toList()
        );
    }

    private ExperienceDto toExperienceDto(Experience experience) {
        return new ExperienceDto(
                experience.getExperienceId(),
                experience.getCorpName(),
                experience.getPosition(),
                experience.getDescr(),
                experience.getStllWrkg(),
                experience.getSdate(),
                experience.getEdate()
        );
    }

    private SkillDto toSkillDto(Skill skill) {
        return new SkillDto(skill.getSkillId(), skill.getName(), skill.getShrtCode());
    }

    private LangDto toLangDto(Lang lang) {
        return new LangDto(lang.getLangId(), lang.getName(), lang.getShrtCode());
    }

    private RuntimeException duplicatePhoneOrRethrow(DataIntegrityViolationException ex) {
        String message = String.valueOf(ex.getMostSpecificCause().getMessage()).toLowerCase();
        if (message.contains("phone")) {
            return new IllegalArgumentException("Bu numara zaten kayıtlı");
        }
        return ex;
    }

    private static boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T userField(User user, String fieldName, Class<T> type) {
        Class<?> typeToScan = user.getClass();
        while (typeToScan != null && typeToScan != Object.class) {
            try {
                java.lang.reflect.Field field = typeToScan.getDeclaredField(fieldName);
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
