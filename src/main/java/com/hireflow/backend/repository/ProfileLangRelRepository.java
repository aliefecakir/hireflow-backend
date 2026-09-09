package com.hireflow.backend.repository;

import com.hireflow.backend.entity.ProfileLangRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Profil-dil köprüsü; lang fetch. */
@Repository
public interface ProfileLangRelRepository extends JpaRepository<ProfileLangRel, UUID> {

    @Query("""
            select r from ProfileLangRel r
            join fetch r.lang
            where r.profile.profileId = :profileId
            """)
    List<ProfileLangRel> findWithLangByProfileId(@Param("profileId") UUID profileId);

    void deleteByProfile_ProfileId(UUID profileId);
}
