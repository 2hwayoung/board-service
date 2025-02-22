package com.example.restapi.global.security;

import com.example.restapi.domain.member.member.entity.Member;
import com.example.restapi.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //require username, password, authorities
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("%s 사용자를 찾을 수 없습니다.".formatted(username))
                );

        return new SecurityUser(member.getId(), member.getUsername(), member.getPassword(),member.getNickname(), member.getAuthorities());
    }
}
