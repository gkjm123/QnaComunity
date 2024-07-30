package com.example.qnacomunity.security;

import com.example.qnacomunity.type.Role;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {

  private final SecretKey secretKey;
  private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

  public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  //토큰 생성
  public String createToken(String loginId, Role role) {
    return Jwts.builder()
        .claim("loginId", loginId)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
        .signWith(secretKey)
        .compact();
  }

  //토큰 파싱(로그인 ID 반환)
  public String getLoginId(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("loginId", String.class);
  }
}
