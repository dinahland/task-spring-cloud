package com.sparta.msa_exam.auth.service;

import com.sparta.msa_exam.auth.dto.SignUpRequestDto;
import com.sparta.msa_exam.auth.dto.SignUpResponseDto;
import com.sparta.msa_exam.auth.entity.User;
import com.sparta.msa_exam.auth.repository.AuthRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;

    /**
     * AuthService 생성자.
     * Base64 URL 인코딩된 비밀 키를 디코딩하여 HMAC-SHA 알고리즘에 적합한 SecretKey 객체를 생성합니다.
     *
     * @param secretKey Base64 URL 인코딩된 비밀 키
     */
    public AuthService(@Value("${service.jwt.secret-key}") String secretKey, AuthRepository authRepository) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.authRepository = authRepository;
    }

    /*username, password 회원 DB와 일치하는지 확인 후 토큰 발급*/
    @Transactional
    public String createAccessToken(SignUpRequestDto requestDto) {
        Optional<User> optional_user = authRepository.findByUsernameAndPassword(requestDto.getUsername(), requestDto.getPassword());
        User user = optional_user.orElseThrow(()->new AuthorizationDeniedException("존재하지 않는 회원입니다."));
        return Jwts.builder()
                // 사용자 ID를 클레임으로 설정
                .claim("user_id", user.getId())
                .claim("role", "ADMIN")
                // JWT 발행자를 설정
                .issuer(issuer)
                // JWT 발행 시간을 현재 시간으로 설정
                .issuedAt(new Date(System.currentTimeMillis()))
                // JWT 만료 시간을 설정
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                // SecretKey를 사용하여 HMAC-SHA512 알고리즘으로 서명
                .signWith(secretKey)
                // JWT 문자열로 컴팩트하게 변환
                .compact();
    }

    /*username, password 회원 DB에 저장 후 응답 DTO 반환*/
    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        User user = new User(requestDto.getUsername(), requestDto.getPassword());
        User savedUser = authRepository.save(user);
        return new SignUpResponseDto(savedUser.getId(), savedUser.getUsername());
    }
}
