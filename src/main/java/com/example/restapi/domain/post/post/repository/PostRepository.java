package com.example.restapi.domain.post.post.repository;

import com.example.restapi.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findTopByOrderByIdDesc();
}
