package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.ScoreHistory;
import com.example.qnacomunity.type.ScoreDescription;
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
public class ScoreHistoryResponse {

  private Long id;
  private Long memberId;
  private String memberNickName;
  private int score;
  private int previous;
  private int remain;
  private ScoreDescription description;
  private Long questionId;
  private String questionTitle;
  private LocalDateTime createdAt;

  public static ScoreHistoryResponse from(ScoreHistory scoreHistory) {
    return ScoreHistoryResponse.builder()
        .id(scoreHistory.getId())
        .memberId(scoreHistory.getMember().getId())
        .memberNickName(scoreHistory.getMember().getNickName())
        .score(scoreHistory.getScore())
        .previous(scoreHistory.getPrevious())
        .remain(scoreHistory.getRemain())
        .description(scoreHistory.getDescription())
        .questionId(scoreHistory.getRelatedQuestion() == null ?
            null : scoreHistory.getRelatedQuestion().getId())
        .questionTitle(scoreHistory.getRelatedQuestion() == null ?
            null : scoreHistory.getRelatedQuestion().getTitle())
        .createdAt(scoreHistory.getCreatedAt())
        .build();
  }
}
