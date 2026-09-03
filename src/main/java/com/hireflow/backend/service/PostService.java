package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreatePostRequest;
import com.hireflow.backend.dto.PostResponse;
import com.hireflow.backend.dto.UpdatePostRequest;

import java.util.List;
import java.util.UUID;

/**
 * İlan (job posting) işlemleri için servis arayüzü
 */
public interface PostService {

    List<PostResponse> getActivePosts(UUID currentUserId);

    List<PostResponse> getAllPostsForAdmin(String statusCode);

    PostResponse getPostById(UUID postId);

    PostResponse createPost(CreatePostRequest request, UUID currentUserId);

    PostResponse updatePost(UUID postId, UpdatePostRequest request, UUID currentUserId);

    PostResponse updatePostStatus(UUID postId, String statusCode, UUID currentUserId);

    void deletePost(UUID postId);
}
