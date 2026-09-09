package com.hireflow.backend.service;

import com.hireflow.backend.dto.LangDto;
import com.hireflow.backend.dto.ProfileDetailResponse;
import com.hireflow.backend.dto.ProfileUpdateRequest;
import com.hireflow.backend.dto.SkillDto;

import java.util.List;
import java.util.UUID;

/** Aday PROFILE, deneyim/skill/dil senkronu ve tamamlanma yüzdesi. */
public interface ProfileService {

    ProfileDetailResponse getMyProfile(UUID currentUserId);

    ProfileDetailResponse updateMyProfile(UUID currentUserId, ProfileUpdateRequest request);

    ProfileDetailResponse getProfileByUserId(UUID targetUserId);

    List<SkillDto> getActiveSkills(String query, int limit);

    List<LangDto> getActiveLanguages();
}
