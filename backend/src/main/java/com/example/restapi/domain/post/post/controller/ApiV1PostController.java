package com.example.restapi.domain.post.post.controller;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.member.member.service.MemberService;
import com.example.restapi.domain.post.post.dto.PageDto;
import com.example.restapi.domain.post.post.dto.PostDto;
import com.example.restapi.domain.post.post.dto.PostWithContentDto;
import com.example.restapi.domain.post.post.entity.Post;
import com.example.restapi.domain.post.post.service.PostService;
import com.example.restapi.global.Rq;
import com.example.restapi.global.dto.RsData;
import com.example.restapi.global.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "ApiV1PostController", description = "글 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    @Operation(
            summary = "글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @GetMapping()
    @Transactional(readOnly = true)
    public RsData<PageDto> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "TITLE") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword) {
        Page<Post> postPage = postService.getListedItems(page, pageSize, keywordType, keyword);

        return new RsData<>(
                "200-1",
                "글 목록 조회가 완료되었습니다.",
                new PageDto(postPage)
        );
    }

    @Operation(
            summary = "내 글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @GetMapping("/mine")
    @Transactional(readOnly = true)
    public RsData<PageDto> getMyItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "title") SearchKeywordType keywordType,
            @RequestParam(defaultValue = "") String keyword) {
        Member actor = rq.getCurrentActor();
        Page<Post> postPage = postService.getMyItems(actor, page, pageSize, keywordType, keyword);

        return new RsData<>(
                "200-1",
                "내 글 목록 조회가 완료되었습니다.",
                new PageDto(postPage)
        );
    }


    @Operation(
            summary = "글 단건 조회",
            description = "비밀글은 작성자만 조회 가능"
    )
    @GetMapping("{id}")
    public RsData<PostWithContentDto> getItem(@PathVariable long id) {
        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );
        if (!post.isPublished()) {
            Member actor = rq.getCurrentActor();
            post.canAccess(actor);
        }

        return new RsData<>(
                "200-1",
                "%d번 글을 조회하였습니다.".formatted(id),
                new PostWithContentDto(post)
        );
    }

    public record writeReqBody(
            @NotBlank String title,
            @NotBlank String content,
            boolean published,
            boolean listed) {}

    @Operation(
            summary = "글 작성",
            description = "로그인 한 사용자만 글 작성 가능"
    )
    @PostMapping()
    public RsData<PostWithContentDto> write(@RequestBody @Valid writeReqBody reqBody) {
        Member actor = rq.getCurrentActor();
        Member realActor = rq.getRealActor(actor);

        Post post = postService.write(realActor, reqBody.title(), reqBody.content(), reqBody.published(), reqBody.listed());

        return new RsData<>(
                "201-1",
                "%d번 글 작성이 완료되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );
    }

    public record modifyReqBody(@NotBlank String title, @NotBlank String content) {}

    @Operation(
            summary = "글 수정",
            description = "작성자와 관리자만 글 수정 가능"
    )
    @PutMapping("{id}")
    @Transactional
    public RsData<PostWithContentDto> modify(@PathVariable long id, @RequestBody @Valid modifyReqBody reqBody) {
        Member actor = rq.getCurrentActor();
        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );
        post.canModify(actor);

        postService.modify(post, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 글 수정이 완료되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );

    }

    @Operation(
            summary = "글 삭제",
            description = "작성자와 관리자만 글 삭제 가능"
    )
    @DeleteMapping("{id}")
    public RsData<PostWithContentDto> delete(@PathVariable long id) {
        Member actor = rq.getCurrentActor();
        Post post = postService.getItem(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글입니다.")
        );
        post.canDelete(actor);

        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글 삭제가 완료되었습니다.".formatted(id)
        );
    }

    public record StatisticsResBody(long postCount, long postPublishedCount, long postListedCount) {}

    @Operation(
            summary = "통계 조회"
    )
    @GetMapping("/statistics")
    public RsData<StatisticsResBody> getStatistics() {
        return new RsData<>(
                "200-1",
                "통계 조회가 완료되었습니다.",
                new StatisticsResBody(
                        10,
                        10,
                        10
                )
        );
    }
}
