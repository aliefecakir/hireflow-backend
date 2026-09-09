package com.hireflow.backend.repository;

import com.hireflow.backend.entity.ProfileSkillRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Profil-skill köprüsü; skill fetch. */
@Repository
public interface ProfileSkillRelRepository extends JpaRepository<ProfileSkillRel, UUID> {

    @Query("""
            select r from ProfileSkillRel r
            join fetch r.skill
            where r.profile.profileId = :profileId
            """)
    List<ProfileSkillRel> findWithSkillByProfileId(@Param("profileId") UUID profileId);

    void deleteByProfile_ProfileId(UUID profileId);
}
