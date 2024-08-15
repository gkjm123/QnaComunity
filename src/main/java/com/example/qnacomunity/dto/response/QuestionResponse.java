package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.Question;
import java.time.LocalDateTime;
import java.util.List;
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
public class QuestionResponse {

  private Long id;
  private String memberName;
  private String title;
  private String content;
  private int reward;
  private List<String> keywords;
  private int hits;
  private LocalDateTime createdAt;

  public static QuestionResponse from(Question question) {
    return QuestionResponse.builder()
        .id(question.getId())
        .memberName(question.getMember().getNickName())
        .title(question.getTitle())
        .content(question.getContent())
        .reward(question.getReward())
        .keywords(question.getKeywords().get("keywords"))
        .hits(question.getHits())
        .createdAt(question.getCreatedAt())
        .build();
  }
}
