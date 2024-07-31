package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.Answer;
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
public class AnswerResponse {

  private Long id;
  private String memberName;
  private Long questionId;
  private String questionTitle;
  private String content;
  private LocalDateTime pickedAt;
  private LocalDateTime createdAt;

  public static AnswerResponse from(Answer answer) {
    return AnswerResponse.builder()
        .id(answer.getId())
        .memberName(answer.getMember().getNickName())
        .questionId(answer.getQuestion().getId())
        .questionTitle(answer.getQuestion().getTitle())
        .content(answer.getContent())
        .pickedAt(answer.getPickedAt())
        .createdAt(answer.getCreatedAt())
        .build();
  }
}
