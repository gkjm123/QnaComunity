package com.example.qnacomunity.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class MemberForm {

  @Getter
  @Builder
  public static class SignUpForm {

    @Size(min = 5, max = 15, message = "양식 오류: 아이디를 5~15자 사이로 입력해주세요.")
    private String loginId;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$", message = "양식 오류: 소문자,대문자,숫자를 포함한 8~20자의 비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passCheck;

    @Size(min = 2, max = 10, message = "양식 오류: 닉네임을 2~10자 사이로 입력해주세요.")
    private String nickName;

    @Email(message = "양식 오류: 이메일을 올바르게 입력해주세요.")
    private String email;
  }

  @Getter
  @Builder
  public static class SignInform {

    @NotBlank(message = "양식 오류: 아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "양식 오류: 비밀번호를 입력해주세요.")
    private String password;
  }

  @Getter
  @Builder
  public static class PassChangeForm {

    @NotBlank(message = "이전 비밀번호를 입력해주세요.")
    private String oldPass;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$", message = "양식 오류: 소문자,대문자,숫자를 포함한 8~20자의 비밀번호를 입력해주세요.")
    private String newPass;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String newPassCheck;
  }

  @Getter
  @Builder
  public static class UpdateInfoForm {

    @Size(min = 2, max = 10, message = "양식 오류: 닉네임을 2~10자 사이로 입력해주세요.")
    private String nickName;

    @Email(message = "양식 오류: 이메일을 올바르게 입력해주세요.")
    private String email;
  }
}
