package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.entity.ScoreHistory;
import com.example.qnacomunity.type.ScoreChangeType;
import com.example.qnacomunity.type.ScoreDescription;
import java.time.LocalDateTime;
import java.util.Optional;
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
  private ScoreChangeType type;
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
        .type(scoreHistory.getType())
        .score(scoreHistory.getScore())
        .previous(scoreHistory.getPrevious())
        .remain(scoreHistory.getRemain())
        .description(scoreHistory.getDescription())
        .questionId(Optional.ofNullable(scoreHistory.getRelatedQuestion())
            .map(Question::getId).orElse(null))
        .questionTitle(Optional.ofNullable(scoreHistory.getRelatedQuestion())
            .map(Question::getTitle).orElse(null))
        .createdAt(scoreHistory.getCreatedAt())
        .build();
  }
}
