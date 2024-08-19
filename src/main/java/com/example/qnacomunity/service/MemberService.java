package com.example.qnacomunity.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.qnacomunity.aop.MemberScoreService;
import com.example.qnacomunity.dto.form.MemberForm.PasswordChangeForm;
import com.example.qnacomunity.dto.form.MemberForm.SignInform;
import com.example.qnacomunity.dto.form.MemberForm.SignUpForm;
import com.example.qnacomunity.dto.form.MemberForm.UpdateInfoForm;
import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.MemberRepository;
import com.example.qnacomunity.security.JwtProvider;
import com.example.qnacomunity.type.Role;
import com.example.qnacomunity.type.ScoreChangeType;
import com.example.qnacomunity.type.ScoreDescription;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtProvider jwtProvider;
  private final AmazonS3 amazonS3;

  private static final int START_SCORE = 50;
  private final MemberScoreService memberScoreService;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public MemberResponse signUp(SignUpForm form, Role role) {

    //비밀번호 확인란 일치 여부
    if (!Objects.equals(form.getPassword(), form.getPasswordCheck())) {
      throw new CustomException(ErrorCode.PASS_CHECK_FAIL);
    }

    //아이디 중복 확인
    if (memberRepository.findByLoginId(form.getLoginId()).isPresent()) {
      throw new CustomException(ErrorCode.ID_EXIST);
    }

    //새로운 멤버 생성
    Member member = getNewMember(form.getLoginId(), form.getNickName(), form.getEmail(), form.getPassword());
    member.setRole(role);
    memberRepository.save(member);

    //시작 스코어(50점) 제공
    member = memberScoreService.change(
        member.getId(),
        ScoreChangeType.PLUS,
        START_SCORE,
        ScoreDescription.JOIN,
        null //연관 질문 없음
    );

    return MemberResponse.from(member);
  }

  @Transactional(readOnly = true)
  public String signIn(SignInform form) {

    //해당 ID의 멤버 존재 여부 확인
    Member member = memberRepository.findByLoginId(form.getLoginId())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));

    //비밀번호 일치 확인
    if (!bCryptPasswordEncoder.matches(form.getPassword(), member.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_FAIL);
    }

    //JWT 토큰 생성
    return jwtProvider.createToken(member.getLoginId(), member.getRole());
  }

  @Transactional
  public MemberResponse updateInfo(MemberResponse memberResponse, UpdateInfoForm form) {

    Member member = getMember(memberResponse);

    //닉네임,이메일 정보 변경
    member.setNickName(form.getNickName());
    member.setEmail(form.getEmail());

    return MemberResponse.from(member);
  }

  @Transactional
  public MemberResponse updatePassword(MemberResponse memberResponse, PasswordChangeForm form) {

    Member member = getMember(memberResponse);

    //이전 비밀번호 일치 체크
    if (!bCryptPasswordEncoder.matches(form.getOldPassword(), member.getPassword())) {
      throw new CustomException(ErrorCode.PASS_NOT_MATCH);
    }

    //새 비밀번호, 새 비밀번호 확인 일치 체크
    if (!Objects.equals(form.getNewPassword(), form.getNewPasswordCheck())) {
      throw new CustomException(ErrorCode.PASS_CHECK_FAIL);
    }

    //비밀번호 업데이트
    member.setPassword(bCryptPasswordEncoder.encode(form.getNewPassword()));
    return MemberResponse.from(memberRepository.save(member));
  }

  @Transactional
  public void saveFile(MemberResponse memberResponse, MultipartFile file) throws IOException {

    Member member = getMember(memberResponse);

    //프로필 사진 업로드 시 S3 에 파일 저장, url 받기
    String originalFilename = file.getOriginalFilename();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());
    amazonS3.putObject(bucket, originalFilename, file.getInputStream(), metadata);
    String url = amazonS3.getUrl(bucket, originalFilename).toString();

    //멤버의 profile_url 설정
    member.setProfileUrl(url);
    memberRepository.save(member);
  }

  @Transactional
  public void delete(MemberResponse memberResponse) {

    memberRepository.deleteById(memberResponse.getId());

  }

  public Member getNewMember(String loginId, String nickName, String email, String password) {

    return Member.builder()
        .loginId(loginId)
        .password(bCryptPasswordEncoder.encode(password))
        .nickName(nickName)
        .email(email)
        .role(Role.ROLE_USER)
        .build();
  }

  public Member getMember(MemberResponse memberResponse) {

    return memberRepository.findById(memberResponse.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
