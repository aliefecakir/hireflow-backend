package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query("""
            select case when count(a) > 0 then true else false end
            from Application a
            where a.post.postId = :postId and a.candidate.userId = :candidateId
            """)
    boolean existsByPost_PostIdAndCandidate_UserId(
            @Param("postId") UUID postId,
            @Param("candidateId") UUID candidateId
    );

    @Query("""
            select distinct a from Application a
            left join fetch a.post
            left join fetch a.candidate
            left join fetch a.status
            where a.candidate.userId = :candidateId
            order by a.cdate desc
            """)
    List<Application> findAllByCandidate_UserIdOrderByCdateDesc(@Param("candidateId") UUID candidateId);

    @Query("""
            select distinct a from Application a
            left join fetch a.post
            left join fetch a.candidate
            left join fetch a.status
            where a.post.postId = :postId
            order by a.cdate desc
            """)
    List<Application> findAllByPost_PostIdOrderByCdateDesc(@Param("postId") UUID postId);

    @Query("""
            select distinct a from Application a
            left join fetch a.post
            left join fetch a.candidate
            left join fetch a.status
            order by a.cdate desc
            """)
    List<Application> findAllByOrderByCdateDesc();

    @Query("""
            select a from Application a
            left join fetch a.post
            left join fetch a.candidate
            left join fetch a.status
            where a.appId = :appId
            """)
    Optional<Application> findDetailedById(@Param("appId") UUID appId);

    @Query("select a.post.postId from Application a where a.candidate.userId = :candidateId")
    List<UUID> findPostIdsByCandidateId(@Param("candidateId") UUID candidateId);
}
