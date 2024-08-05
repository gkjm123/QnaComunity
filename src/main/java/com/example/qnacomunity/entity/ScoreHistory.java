package com.example.qnacomunity.entity;

import com.example.qnacomunity.type.ScoreChangeType;
import com.example.qnacomunity.type.ScoreDescription;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @Enumerated(EnumType.STRING)
  private ScoreChangeType type;

  private int score;
  private int previous;
  private int remain;

  @Enumerated(EnumType.STRING)
  private ScoreDescription description;

  @ManyToOne(fetch = FetchType.LAZY)
  private Question relatedQuestion;

  @CreationTimestamp
  private LocalDateTime createdAt;

}
