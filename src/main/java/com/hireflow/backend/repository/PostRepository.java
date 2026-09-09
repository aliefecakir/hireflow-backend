package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** POST ilanları; statüye göre liste. */
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByOrderByCdateDesc();

    List<Post> findAllByStIdOrderByCdateDesc(Long stId);
}
