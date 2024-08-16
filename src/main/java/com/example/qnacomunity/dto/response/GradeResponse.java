package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.Grade;
import java.time.LocalDateTime;
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
public class GradeResponse {

  private Long id;
  private String gradeName;
  private int minScore;
  private LocalDateTime createdAt;

  public static GradeResponse from(Grade grade) {

    return GradeResponse.builder()
        .id(grade.getId())
        .gradeName(grade.getGradeName())
        .minScore(grade.getMinScore())
        .createdAt(grade.getCreatedAt())
        .build();
  }
}
