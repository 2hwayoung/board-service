package com.example.restapi.domain.post.post.controller;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.post.post.dto.PostDto;
import com.example.restapi.domain.post.post.entity.Post;
import com.example.restapi.domain.post.post.service.PostService;
import com.example.restapi.global.Rq;
import com.example.restapi.global.dto.RsData;
import com.example.restapi.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;
    private final Rq rq;

    @GetMapping("{id}")
    public RsData<PostDto> getItem(@PathVariable long id) {
        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );

        return new RsData<>(
                "200-1",
                "%d번 글을 조회하였습니다.".formatted(id),
                new PostDto(post)
        );
    }

    public record writeReqBody(@NotBlank String title, @NotBlank String content) {}

    @PostMapping()
    public RsData<PostDto> write(@RequestBody @Valid writeReqBody reqBody) {
        Member actor = rq.getAuthenticatedActor();
        Post post = postService.write(actor, reqBody.title(), reqBody.content());
        return new RsData<>(
                "201-1",
                "%d번 글 작성이 완료되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }

    public record modifyReqBody(@NotBlank String title, @NotBlank String content) {}

    @PutMapping("{id}")
    public RsData<PostDto> modify(@PathVariable long id, @RequestBody @Valid modifyReqBody reqBody) {
        Member actor = rq.getAuthenticatedActor();
        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );
        post.canModify(actor);

        postService.modify(post, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 글 수정이 완료되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );

    }
}
