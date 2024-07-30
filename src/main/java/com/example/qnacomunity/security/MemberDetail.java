package com.example.qnacomunity.security;

import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.entity.Member;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
public class MemberDetail implements CustomUserDetail {

  private final MemberResponse memberResponse;

  @Override
  public String getUsername() {
    return memberResponse.getLoginId();
  }

  @Override
  public String getPassword() {
    return memberResponse.getPassword();
  }

  public String getName() {
    return memberResponse.getNickName();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(memberResponse.getRole().toString()));
  }

  @Override
  public MemberResponse getMemberResponse() {
    return memberResponse;
  }
}
