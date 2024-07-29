package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.MemberForm;
import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.security.CustomUserDetail;
import com.example.qnacomunity.service.MemberService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
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

  //회원 가입
  @PostMapping("/sign-up")
  public ResponseEntity<?> signUp(@Valid @RequestBody MemberForm.SignUpForm form, Errors errors) {

    //회원 가입 양식 에러 체크
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
    }

    return ResponseEntity.ok(memberService.signUp(form));
  }

  //로그인
  @GetMapping("/sign-in")
  public ResponseEntity<String> signIn(@Valid @RequestBody MemberForm.SignInform form,
      Errors errors) {

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
    }

    //JWT 토큰 생성
    String token = memberService.signIn(form);

    return ResponseEntity.ok(token);
  }

  //멤버 역할 확인
  @GetMapping("/get-role")
  public ResponseEntity<String> getRole(@AuthenticationPrincipal CustomUserDetail cd) {
    if (cd == null) {
      return ResponseEntity.ok("비회원");

    } else {

      if (Objects.equals(cd.getMember().getRole(), "ROLE_MANAGER")) {
        return ResponseEntity.ok("관리자");

      } else {
        return ResponseEntity.ok("회원");

      }
    }
  }

  //멤버 정보 확인
  @GetMapping("/get-info")
  public ResponseEntity<MemberResponse> getInfo(@AuthenticationPrincipal CustomUserDetail cd) {
    return ResponseEntity.ok(memberService.getInfo(cd.getMember()));
  }

  //멤버 정보 수정
  @PutMapping("/update-info")
  public ResponseEntity<?> updateInfo(@AuthenticationPrincipal CustomUserDetail cd,
      @Valid @RequestBody MemberForm.UpdateInfoForm form, Errors errors
  ) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
    }
    return ResponseEntity.ok(memberService.updateInfo(cd.getMember(), form));
  }

  //프로필 사진 변경
  @PutMapping("/update-profile")
  public ResponseEntity<String> updateProfile(
      @AuthenticationPrincipal CustomUserDetail cd,
      @RequestParam MultipartFile file) throws IOException {

    if (!file.isEmpty()) {
      memberService.saveFile(cd.getMember(), file);
    }

    return ResponseEntity.ok("프로필 업데이트 완료");
  }

  //패스워드 변경
  @PutMapping("/update-pass")
  public ResponseEntity<?> updatePass(@AuthenticationPrincipal CustomUserDetail cd,
      @Valid @RequestBody MemberForm.PassChangeForm form,
      Errors errors
  ) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors.getAllErrors().get(0).getDefaultMessage());
    }
    return ResponseEntity.ok(memberService.updatePass(cd.getMember(), form));
  }

  //회원 탈퇴
  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(@AuthenticationPrincipal CustomUserDetail cd) {
    memberService.delete(cd.getMember());
    return ResponseEntity.ok("회원 탈퇴 완료");
  }
}
