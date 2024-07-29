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
import com.example.qnacomunity.security.JwtUtil;
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
  private JwtUtil jwtUtil;

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
        .passCheck("asdfasdf123!")
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role("user")
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findByLoginId(anyString()))
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
        .role("user")
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(memberRepository.findByLoginIdAndDeletedAtIsNull(anyString()))
        .willReturn(Optional.of(member));

    given(bCryptPasswordEncoder.matches(any(), any()))
        .willReturn(true);

    given(jwtUtil.createToken(any(), any()))
        .willReturn("token");

    //when
    String token = memberService.signIn(signInForm);

    //then
    assertEquals("token", token);
  }

  @Test
  void getInfo() {

    //given
    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role("user")
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    //when
    MemberResponse memberResponse = memberService.getInfo(member);

    //then
    assertEquals("abc", memberResponse.getNickName());

  }

  @Test
  void updateInfo() {

    //given
    MemberForm.UpdateInfoForm signUpForm = MemberForm.UpdateInfoForm.builder()
        .email("abc@naver.com")
        .nickName("abc")
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role("user")
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    given(bCryptPasswordEncoder.encode(any()))
        .willReturn("encodedPassword");

    //when
    MemberResponse memberResponse = memberService.updateInfo(member, signUpForm);

    //then
    assertEquals("abc", memberResponse.getNickName());
  }

  @Test
  void updatePass() {
    //given
    MemberForm.PassChangeForm passChangeForm = MemberForm.PassChangeForm.builder()
        .oldPass("asdfasdf123!")
        .newPass("newpass123!")
        .newPassCheck("newpass123!")
        .build();

    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role("user")
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

    //when
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    memberService.updatePass(member, passChangeForm);

    //then
    verify(memberRepository, times(1)).save(captor.capture());
    assertEquals("newPassword", captor.getValue().getPassword());
  }

  @Test
  void delete() {

    //given
    Member member = Member.builder()
        .loginId("abc")
        .password("1")
        .profileUrl("url")
        .role("user")
        .score(0)
        .nickName("abc")
        .email("abc@naver.com")
        .createdAt(LocalDateTime.now())
        .build();

    //when
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    memberService.delete(member);

    //then
    verify(memberRepository, times(1)).save(captor.capture());
    assertEquals("abc", captor.getValue().getNickName());
  }
}