package com.example.restapi.domain.member.member.controller;

import com.example.restapi.domain.member.member.dto.MemberDto;
import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.member.member.service.MemberService;
import com.example.restapi.domain.post.post.service.PostService;
import com.example.restapi.global.Rq;
import com.example.restapi.global.dto.Empty;
import com.example.restapi.global.dto.RsData;
import com.example.restapi.global.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ApiV1MemberController", description = "회원 관련 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {

    private final MemberService memberService;
    private final Rq rq;

    public record JoinReqBody(@NotBlank String username, @NotBlank String password, @NotBlank String nickname) {}

    @Operation(summary = "회원 가입")
    @PostMapping(value = "/join", produces = "application/json;charset=UTF-8")
    public RsData<MemberDto> join(@RequestBody @Valid JoinReqBody reqBody) {

        memberService.findByUsername(reqBody.username())
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
                });


        Member member = memberService.join(reqBody.username(), reqBody.password(), reqBody.nickname());
        return new RsData<>(
                "201-1",
                "회원 가입이 완료되었습니다.",
                new MemberDto(member)
                );
    }


    public record LoginReqBody(@NotBlank String username, @NotBlank String password) {}

    public record LoginResBody(@NonNull MemberDto item, @NonNull String apiKey, @NonNull String accessToken) {}

    @Operation(summary = "로그인", description = "로그인 성공 시 ApiKey와 AccessToken 반환. 쿠키로도 반환")
    @PostMapping("/login")
    public RsData<LoginResBody> login(@RequestBody @Valid LoginReqBody reqBody, HttpServletResponse response) {

        Member member = memberService.findByUsername(reqBody.username()).orElseThrow(
                () -> new ServiceException("401-1", "잘못된 아이디입니다.")
        );

        if(!member.getPassword().equals(reqBody.password())) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = memberService.genAccessToken(member);

        rq.addCookie("accessToken", accessToken);
        rq.addCookie("apiKey", member.getApiKey());

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getNickname()),
                new LoginResBody(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 쿠키 삭제")
    @DeleteMapping("/logout")
    public RsData<Empty> logout() {

        rq.removeCookie("accessToken");
        rq.removeCookie("apiKey");

        return new RsData<>("200-1", "로그아웃 되었습니다.");
    }


    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public RsData<MemberDto> me() {
        Member actor = rq.getCurrentActor();
        Member realActor = rq.getRealActor(actor);

        return new RsData<>(
                "200-1",
                "내 정보 조회가 완료되었습니다.",
                new MemberDto(realActor)
        );
    }


}
