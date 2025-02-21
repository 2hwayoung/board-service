package com.example.restapi.domain.post.post.service;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.post.post.controller.SearchKeywordType;
import com.example.restapi.domain.post.post.entity.Post;
import com.example.restapi.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content, boolean published, boolean listed) {

        return postRepository.save(
                Post
                        .builder()
                        .author(author)
                        .title(title)
                        .content(content)
                        .published(published)
                        .listed(listed)
                        .build()
        );
    }

    public List<Post> getItems() {
        return postRepository.findAll();
    }

    public Page<Post> getListedItems(int page, int pageSize, SearchKeywordType keywordType, String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

        if (keyword.isBlank()) {
            return postRepository.findByListed(true, pageRequest);
        }

        String likeKeyword = "%" + keyword + "%";

        return switch (keywordType) {
            case SearchKeywordType.CONTENT -> postRepository.findByListedAndContentLike(true, likeKeyword, pageRequest);
            case SearchKeywordType.TITLE -> postRepository.findByListedAndTitleLike(true, likeKeyword, pageRequest);
            default -> postRepository.findByListed(true, pageRequest);
        };
    }

    public Page<Post> getMyItems(Member author, int page, int pageSize, SearchKeywordType keywordType, String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

        if (keyword.isBlank()) {
            return postRepository.findByAuthor(author, pageRequest);
        }

        String likeKeyword = "%" + keyword + "%";

        return switch (keywordType) {
            case SearchKeywordType.CONTENT -> postRepository.findByAuthorAndContentLike(author, likeKeyword, pageRequest);
            case SearchKeywordType.TITLE -> postRepository.findByAuthorAndTitleLike(author, likeKeyword, pageRequest);
            default -> postRepository.findByAuthor(author, pageRequest);
        };
    }

    public Optional<Post> getItem(long id) {
        return postRepository.findById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
    }

    public void flush() {
        postRepository.flush();
    }

    public Optional<Post> getLatestItem() {
        return postRepository.findTopByOrderByIdDesc();
    }
}
