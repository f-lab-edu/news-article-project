package com.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            System.out.println("🚨 요청에 쿠키가 없음");
//        } else {
//            System.out.println("✅ 쿠키 개수: " + cookies.length);
//        }

        if (cookies != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .findFirst();

            if (jwtCookie.isPresent()) {
                String token = jwtCookie.get().getValue();
//                System.out.println("✅ JWT 토큰 추출 성공: " + token);

                String email = jwtUtil.extractEmail(token);
//                if (email == null) {
//                    System.out.println("🚨 JWT에서 이메일 추출 실패");
//                } else {
//                    System.out.println("✅ JWT에서 이메일 추출 성공: " + email);
//                }

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtUtil.validateToken(token)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
//                        System.out.println("✅ SecurityContext에 인증 정보 설정 완료");
                    }
//                    else {
//                        System.out.println("🚨 JWT 토큰 유효성 검사 실패");
//                    }
//                    System.out.println("✅ SecurityContext 설정됨: " + SecurityContextHolder.getContext().getAuthentication());
                }
//                else {
//                    System.out.println("🚨 JWT 쿠키를 찾을 수 없음");
//                }
            }
        }
        chain.doFilter(request, response);
    }
}
