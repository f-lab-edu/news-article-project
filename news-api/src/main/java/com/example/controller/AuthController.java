package com.example.controller;

import com.example.config.JwtUtil;
import com.example.dto.LoginRequestDTO;
import com.example.service.CustomUserDetailsService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // ✅ Set-Cookie 헤더를 추가하여 JWT를 쿠키에 저장
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)  // 자바스크립트에서 접근 불가능
                .secure(false)   // HTTPS 환경에서 true로 설정 (개발 중이라 false)
                .path("/")       // 모든 경로에서 쿠키 사용 가능
                .maxAge(3600)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString()) // 쿠키를 HTTP 응답에 포함
                .body(Map.of("message", "Login successful"));
    }

}
