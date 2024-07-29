package com.example.qnacomunity.security;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Oauth2UserInfoGoogle implements Oauth2UserInfo {

  private final Map<String, Object> atr;

  @Override
  public String getName() {
    return atr.get("name").toString();
  }

  @Override
  public String getEmail() {
    return atr.get("email").toString();
  }

  @Override
  public String getProviderId() {
    return atr.get("sub").toString();
  }
}
