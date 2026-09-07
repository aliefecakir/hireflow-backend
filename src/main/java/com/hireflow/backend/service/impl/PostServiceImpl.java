package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreatePostRequest;
import com.hireflow.backend.dto.PostResponse;
import com.hireflow.backend.dto.UpdatePostRequest;
import com.hireflow.backend.entity.GnlSt;
import com.hireflow.backend.entity.Post;
import com.hireflow.backend.repository.ApplicationRepository;
import com.hireflow.backend.repository.GnlStRepository;
import com.hireflow.backend.repository.PostRepository;
import com.hireflow.backend.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PostService implementasyonu — ilan statüleri GNL_ST üzerinden yönetilir
 */
@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private static final String POST_ENTITY_CODE = "POST";

    private final PostRepository postRepository;
    private final GnlStRepository gnlStRepository;
    private final ApplicationRepository applicationRepository;

    public PostServiceImpl(
            PostRepository postRepository,
            GnlStRepository gnlStRepository,
            ApplicationRepository applicationRepository
    ) {
        this.postRepository = postRepository;
        this.gnlStRepository = gnlStRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<PostResponse> getActivePosts(UUID currentUserId) {
        GnlSt activeStatus = resolvePostStatus("ACTV");
        Set<UUID> appliedPostIds = currentUserId == null
                ? Set.of()
                : new HashSet<>(applicationRepository.findPostIdsByCandidateId(currentUserId));
        return toResponseList(
                postRepository.findAllByStIdOrderByCdateDesc(activeStatus.getGnlStId()),
                appliedPostIds
        );
    }

    @Override
    public List<PostResponse> getAllPostsForAdmin(String statusCode) {
        List<Post> posts;
        if (statusCode == null || statusCode.isBlank()) {
            posts = postRepository.findAllByOrderByCdateDesc();
        } else {
            GnlSt status = resolvePostStatus(statusCode);
            posts = postRepository.findAllByStIdOrderByCdateDesc(status.getGnlStId());
        }
        return toResponseList(posts, Set.of());
    }

    @Override
    public PostResponse getPostById(UUID postId) {
        Post post = findPostOrThrow(postId);
        GnlSt status = post.getStId() == null ? null : gnlStRepository.findById(post.getStId()).orElse(null);
        return toResponse(post, status, false);
    }

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request, UUID currentUserId) {
        GnlSt status = resolvePostStatus(request.statusCode());

        Post post = new Post();
        post.setTitle(request.title());
        post.setDescr(request.descr());
        post.setReqTech(request.reqTech());
        post.setReqDept(request.reqDept());
        post.setStId(status.getGnlStId());
        post.setCuser(currentUserId);

        return toResponse(postRepository.save(post), status, false);
    }

    @Override
    @Transactional
    public PostResponse updatePost(UUID postId, UpdatePostRequest request, UUID currentUserId) {
        Post post = findPostOrThrow(postId);
        GnlSt status = resolvePostStatus(request.statusCode());

        post.setTitle(request.title());
        post.setDescr(request.descr());
        post.setReqTech(request.reqTech());
        post.setReqDept(request.reqDept());
        post.setStId(status.getGnlStId());
        post.setUuser(currentUserId);

        return toResponse(postRepository.save(post), status, false);
    }

    @Override
    @Transactional
    public PostResponse updatePostStatus(UUID postId, String statusCode, UUID currentUserId) {
        Post post = findPostOrThrow(postId);
        GnlSt status = resolvePostStatus(statusCode);

        post.setStId(status.getGnlStId());
        post.setUuser(currentUserId);

        return toResponse(postRepository.save(post), status, false);
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }

    private Post findPostOrThrow(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    private GnlSt resolvePostStatus(String statusCode) {
        return gnlStRepository.findByEntCodeNameAndShrtCode(POST_ENTITY_CODE, statusCode)
                .orElseThrow(() -> new RuntimeException(
                        "Status not found for entity POST with code: " + statusCode));
    }

    private List<PostResponse> toResponseList(List<Post> posts, Set<UUID> appliedPostIds) {
        Set<Long> statusIds = posts.stream()
                .map(Post::getStId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, GnlSt> statusesById = gnlStRepository.findAllById(statusIds).stream()
                .collect(Collectors.toMap(GnlSt::getGnlStId, Function.identity()));

        return posts.stream()
                .map(post -> toResponse(
                        post,
                        statusesById.get(post.getStId()),
                        appliedPostIds.contains(post.getPostId())
                ))
                .toList();
    }

    private PostResponse toResponse(Post post, GnlSt status, boolean applied) {
        PostResponse.Status statusDto = status == null
                ? null
                : new PostResponse.Status(status.getShrtCode(), status.getName());

        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getDescr(),
                post.getReqTech(),
                post.getReqDept(),
                statusDto,
                applied,
                post.getCdate(),
                post.getUdate()
        );
    }
}
