package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.MemberForm;
import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.dto.response.ScoreHistoryResponse;
import com.example.qnacomunity.repository.ScoreHistoryRepository;
import com.example.qnacomunity.security.CustomUserDetail;
import com.example.qnacomunity.service.MemberService;
import com.example.qnacomunity.type.Role;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final ScoreHistoryRepository scoreHistoryRepository;

  @Operation(summary = "일반 회원 가입")
  @PostMapping("/registration")
  public ResponseEntity<?> signUp(@Valid @RequestBody MemberForm.SignUpForm form) {

    return ResponseEntity.ok(memberService.signUp(form, Role.ROLE_USER));
  }

  @Operation(summary = "매니저 회원 가입")
  @PostMapping("/manager-registration")
  public ResponseEntity<?> managerSignUp(@Valid @RequestBody MemberForm.SignUpForm form) {

    return ResponseEntity.ok(memberService.signUp(form, Role.ROLE_MANAGER));
  }

  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<String> signIn(@Valid @RequestBody MemberForm.SignInform form) {

    //JWT 토큰 생성
    String token = memberService.signIn(form);

    return ResponseEntity.ok(token);
  }

  @Operation(summary = "역할(ROLE) 확인")
  @GetMapping("/role")
  public ResponseEntity<String> getRole(@AuthenticationPrincipal CustomUserDetail userDetail) {
    if (userDetail == null) {
      return ResponseEntity.ok("비회원");

    } else {

      if (Objects.equals(userDetail.getMemberResponse().getRole(), Role.ROLE_MANAGER)) {
        return ResponseEntity.ok("관리자");

      } else {
        return ResponseEntity.ok("회원");

      }
    }
  }

  @Operation(summary = "멤버 등급 확인")
  @GetMapping("/info")
  public ResponseEntity<MemberResponse> getInfo(@AuthenticationPrincipal CustomUserDetail userDetail) {
    return ResponseEntity.ok(userDetail.getMemberResponse());
  }

  @Operation(summary = "멤버 정보 수정")
  @PutMapping("/info")
  public ResponseEntity<?> updateInfo(@AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody MemberForm.UpdateInfoForm form
  ) {

    return ResponseEntity.ok(memberService.updateInfo(userDetail.getMemberResponse(), form));
  }

  @Operation(summary = "프로필 사진 변경")
  @PutMapping("/profile")
  public ResponseEntity<String> updateProfile(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @RequestParam MultipartFile file) throws IOException {

    if (!file.isEmpty()) {
      memberService.saveFile(userDetail.getMemberResponse(), file);
    }

    return ResponseEntity.ok("프로필 업데이트 완료");
  }

  @Operation(summary = "비밀번호 변경")
  @PutMapping("/password")
  public ResponseEntity<?> updatePassword(@AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody MemberForm.PasswordChangeForm form
  ) {

    return ResponseEntity.ok(memberService.updatePassword(userDetail.getMemberResponse(), form));
  }

  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/removal")
  public ResponseEntity<String> delete(@AuthenticationPrincipal CustomUserDetail userDetail) {
    memberService.delete(userDetail.getMemberResponse());
    return ResponseEntity.ok("회원 탈퇴 완료");
  }

  @Operation(summary = "스코어 변경 내역 확인")
  @GetMapping("/score-histories")
  public ResponseEntity<Page<ScoreHistoryResponse>> getScoreHistories(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(
        scoreHistoryRepository.findAllByMember_Id(userDetail.getMemberResponse().getId(), pageable)
            .map(ScoreHistoryResponse::from)
    );
  }
}
