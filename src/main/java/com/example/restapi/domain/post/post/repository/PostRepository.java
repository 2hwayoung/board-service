package com.example.restapi.domain.post.post.repository;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findTopByOrderByIdDesc();
    Page<Post> findByListed(boolean listed, PageRequest pageRequest);
    Page<Post> findByListedAndTitleLike(boolean listed, String likeKeyword, PageRequest pageRequest);
    Page<Post> findByListedAndContentLike(boolean listed, String likeKeyword, PageRequest pageRequest);
    Page<Post> findByAuthor(Member author, PageRequest pageRequest);
    Page<Post> findByAuthorAndContentLike(Member author, String likeKeyword, PageRequest pageRequest);
    Page<Post> findByAuthorAndTitleLike(Member author, String likeKeyword, PageRequest pageRequest);
}
