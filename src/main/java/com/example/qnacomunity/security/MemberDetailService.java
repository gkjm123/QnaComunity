package com.example.qnacomunity.security;

import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public CustomUserDetail loadUserByUsername(String loginId) {
    Member member = memberRepository.findByLoginIdAndDeletedAtIsNull(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

    return new MemberDetail(member);
  }
}
