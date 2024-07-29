package com.example.qnacomunity.security;

import com.example.qnacomunity.entity.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class Oauth2UserDetail implements CustomUserDetail, OAuth2User {

  private final Map<String, Object> attributes;
  private final Member member;

  @Override
  public String getUsername() {
    return member.getLoginId();
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getName() {
    return member.getNickName();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(member.getRole()));
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Member getMember() {
    return member;
  }
}
