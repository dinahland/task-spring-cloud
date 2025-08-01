package com.sparta.msa_exam.auth.controller;

import com.sparta.msa_exam.auth.dto.SignUpRequestDto;
import com.sparta.msa_exam.auth.dto.SignUpResponseDto;
import com.sparta.msa_exam.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* 로그인 API */
    @PostMapping("/auth/sign-in")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignUpRequestDto requestDto) {
        return ResponseEntity.ok(new AuthResponse(authService.createAccessToken(requestDto)));
    }

    /*회원 가입 API*/
    @PostMapping("/auth/sign-up")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto requestDto){
        return ResponseEntity.ok(authService.signUp(requestDto));
    }

    /**
     * JWT 액세스 토큰을 포함하는 응답 객체입니다.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class AuthResponse {
        private String access_token;

    }
}