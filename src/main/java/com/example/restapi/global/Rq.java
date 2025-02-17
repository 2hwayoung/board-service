package com.example.restapi.global;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.member.member.service.MemberService;
import com.example.restapi.global.exception.ServiceException;
import com.example.restapi.global.security.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Optional;

// Request, Response, Session, Cookie, Header
@Component
@RequiredArgsConstructor
@RequestScope
public class Rq {

    private final HttpServletRequest request;
    private final MemberService memberService;

    public void setLogin(Member actor) {
        UserDetails user = new SecurityUser(actor.getId(), actor.getUsername(), "", List.of());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    public Member getCurrentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new ServiceException("401-2", "로그인이 필요합니다.");
        }

        Object principal = auth.getPrincipal();

        if(!(principal instanceof SecurityUser user)) {
            throw new ServiceException("401-3", "잘못된 인증 정보입니다");
        }

        return Member.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
