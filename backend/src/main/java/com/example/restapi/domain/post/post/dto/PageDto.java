package com.example.restapi.domain.post.post.dto;

import com.example.restapi.domain.post.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto {
    @NonNull
    private List<PostDto> items;
    @NonNull
    private int currentPageNo;
    @NonNull
    private int totalPages;
    @NonNull
    private int totalItems;
    @NonNull
    private int pageSize;

    public PageDto(Page<Post> postPage) {
        this.items = postPage.getContent().stream()
                .map(PostDto::new)
                .toList();
        this.currentPageNo = postPage.getNumber() + 1;
        this.totalPages = postPage.getTotalPages();
        this.totalItems = (int) postPage.getTotalElements();
        this.pageSize = postPage.getSize();
    }
}
