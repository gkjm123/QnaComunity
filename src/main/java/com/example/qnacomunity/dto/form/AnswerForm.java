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
public class AnswerForm {

  @Min(value = 1, message = "질문 ID 를 입력해주세요.")
  private Long questionId;

  @Size(min = 5, max = 200, message = "양식 오류: 내용은 5~200자 사이로 입력해주세요.")
  private String content;
}
