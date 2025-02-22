package com.example.restapi.global;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.member.member.service.MemberService;
import com.example.restapi.global.exception.ServiceException;
import com.example.restapi.global.security.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void setLogin(Member actor) {
        UserDetails user = new SecurityUser(actor.getId(), actor.getUsername(), "", actor.getNickname(), actor.getAuthorities());

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
                .nickname(user.getNickname())
                .build();
    }
        public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String getValueFromCookie(String name) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            return null;
        }

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void addCookie(String name, String value) {
        Cookie accsessTokenCookie = new Cookie(name, value);

        accsessTokenCookie.setDomain("localhost");
        accsessTokenCookie.setPath("/");
        accsessTokenCookie.setHttpOnly(true);
        accsessTokenCookie.setSecure(true);
        accsessTokenCookie.setAttribute("SameSite", "Strict");

        response.addCookie(accsessTokenCookie);
    }

      public Member getRealActor(Member actor) {
        return memberService.findById(actor.getId()).get();
    }

    public void removeCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}
