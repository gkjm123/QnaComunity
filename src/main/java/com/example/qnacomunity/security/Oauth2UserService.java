package com.example.qnacomunity.security;

import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.repository.MemberRepository;
import com.example.qnacomunity.service.MemberService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final MemberService memberService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    //OAuth2 로그인 후 회원 정보를 넘겨 받음
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    Map<String, Object> atr = oAuth2User.getAttributes();

    Oauth2UserInfo oauth2UserInfo = null;

    // 구글(이후 다른 Oauth2 로그인 추가 가능)
    if (provider.equals("google")) {
      oauth2UserInfo = new Oauth2UserInfoGoogle(atr);
    }

    String loginId = provider + "_" + oauth2UserInfo.getProviderId();
    String nickName = oauth2UserInfo.getName();
    String email = oauth2UserInfo.getEmail();

    Optional<Member> optionalMember = memberRepository.findByLoginIdAndDeletedAtIsNull(loginId);

    //회원 DB 에 정보가 없다면 회원 가입 진행
    if (optionalMember.isEmpty()) {
      Member member = memberService.getNewMember(loginId, nickName, email, null);
      member = memberRepository.save(member);

      //새로 가입된 회원 UserDetail 반환
      return new Oauth2UserDetail(atr, member);
    }

    //회원 정보가 있으면 해당 회원 UserDetail 반환
    return new Oauth2UserDetail(atr, optionalMember.get());
  }
}