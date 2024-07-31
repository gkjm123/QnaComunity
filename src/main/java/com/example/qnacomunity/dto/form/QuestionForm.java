package com.example.qnacomunity.dto.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionForm {

  @Size(min = 5, max = 20, message = "양식 오류: 제목은 5~20자 사이로 입력해주세요.")
  private String title;

  @Size(min = 10, max = 100, message = "양식 오류: 내용은 10~100자 사이로 입력해주세요.")
  private String content;

  private String keywords;

  @Min(value = 5, message = "양식 오류: 보상 포인트를 5점 이상 입력해주세요.")
  private int reward;
}
