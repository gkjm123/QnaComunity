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
public class GradeForm {

  @Size(min = 2, max = 10, message = "양식 오류: 등급 이름은 2~10 자 사이로 입력해주세요.")
  private String gradeName;

  @Min(value = 0, message = "최소 스코어를 0점 이상 입력해주세요.")
  private int minScore;

}
