package com.hireflow.backend.repository;

import com.hireflow.backend.entity.ProfileExpRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Profil-deneyim köprüsü; experience fetch. */
@Repository
public interface ProfileExpRelRepository extends JpaRepository<ProfileExpRel, UUID> {

    @Query("""
            select r from ProfileExpRel r
            join fetch r.experience e
            where r.profile.profileId = :profileId
            order by e.cdate desc
            """)
    List<ProfileExpRel> findWithExperienceByProfileId(@Param("profileId") UUID profileId);

    void deleteByProfile_ProfileId(UUID profileId);
}
