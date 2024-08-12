package com.example.qnacomunity.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rank {

  private int rank;
  private String subject;
  private int score;

  public Rank(String subject, int score) {
    this.subject = subject;
    this.score = score;
  }
}
