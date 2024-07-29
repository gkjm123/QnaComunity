package com.example.qnacomunity.security;

import com.example.qnacomunity.entity.Member;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
public class MemberDetail implements CustomUserDetail {

  private final Member member;

  @Override
  public String getUsername() {
    return member.getLoginId();
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  public String getName() {
    return member.getNickName();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(member.getRole()));
  }

  @Override
  public Member getMember() {
    return member;
  }
}
