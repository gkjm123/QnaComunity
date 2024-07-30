package com.example.qnacomunity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.qnacomunity.dto.form.MemberForm;
import com.example.qnacomunity.dto.form.MemberForm.SignUpForm;
import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.repository.MemberRepository;
import com.example.qnacomunity.security.JwtProvider;
import com.example.qnacomunity.type.Role;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Mock
  private JwtProvider jwtProvider;

  @InjectMocks
  private MemberService memberService;

  @Test
  void signUp() {
    //given
    SignUpForm signUpForm = SignUpForm.builder()
        .loginId("abc")
        .password("asdfasdf123!")
        .email("abc@naver.com")
        .nickName("abc")
        .passwordCheck("asdfasdf123!")
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findByLoginIdAndDeletedAtIsNull(anyString()))
        .willReturn(Optional.empty());

    given(memberRepository.save(any()))
        .willReturn(member);

    given(bCryptPasswordEncoder.encode(any()))
        .willReturn("encodedPassword");

    //when
    MemberResponse memberResponse = memberService.signUp(signUpForm);

    //then
    assertEquals("abc", memberResponse.getNickName());
  }

  @Test
  void signIn() {

    //given
    MemberForm.SignInform signInForm = MemberForm.SignInform.builder()
        .loginId("abc")
        .password("asdfasdf123!")
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findByLoginIdAndDeletedAtIsNull(anyString()))
        .willReturn(Optional.of(member));

    given(bCryptPasswordEncoder.matches(any(), any()))
        .willReturn(true);

    given(jwtProvider.createToken(any(), any()))
        .willReturn("token");

    //when
    String token = memberService.signIn(signInForm);

    //then
    assertEquals("token", token);
  }

  @Test
  void updateInfo() {

    //given
    MemberForm.UpdateInfoForm signUpForm = MemberForm.UpdateInfoForm.builder()
        .email("abc@naver.com")
        .nickName("abc")
        .build();

    MemberResponse member = MemberResponse.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findById(any()))
        .willReturn(Optional.ofNullable(Member.builder().nickName("abc").build()));

    //when
    MemberResponse memberResponse = memberService.updateInfo(member, signUpForm);

    //then
    assertEquals("abc", memberResponse.getNickName());
  }

  @Test
  void updatePassword() {
    //given
    MemberForm.PasswordChangeForm passwordChangeForm = MemberForm.PasswordChangeForm.builder()
        .oldPassword("asdfasdf123!")
        .newPassword("newpass123!")
        .newPasswordCheck("newpass123!")
        .build();

    MemberResponse memberResponse = MemberResponse.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.save(any()))
        .willReturn(member);

    given(bCryptPasswordEncoder.matches(any(), any()))
        .willReturn(true);

    given(bCryptPasswordEncoder.encode(any()))
        .willReturn("newPassword");

    given(memberRepository.findById(any()))
        .willReturn(Optional.of(member));

    //when
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    memberService.updatePassword(memberResponse, passwordChangeForm);

    //then
    verify(memberRepository, times(1)).save(captor.capture());
    assertEquals("newPassword", captor.getValue().getPassword());
  }

  @Test
  void delete() {

    //given
    MemberResponse memberResponse = MemberResponse.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role(Role.ROLE_USER)
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findById(any()))
        .willReturn(Optional.of(member));

    //when
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    memberService.delete(memberResponse);

    //then
    verify(memberRepository, times(1)).save(captor.capture());
    assertEquals("abc", captor.getValue().getNickName());
  }
}