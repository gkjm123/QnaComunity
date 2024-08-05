package com.example.qnacomunity.aop;

import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.type.ScoreDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockService {
  private final MemberScoreService memberScoreService;
  private final QuestionHitService questionHitService;

  @ScoreLock
  public Member changeScore(
      Long memberId, int score, ScoreDescription description, Question relatedQuestion
  ) {

    //락 획득 후 내부의 트랜잭션 메서드로 진행
    return memberScoreService.change(memberId, score, description, relatedQuestion);
    //트랜잭션 종료되며 커밋 실행 -> 락 해제 -> 다음 락 획득자가 이전 변경 사항을 읽어 올 수 있다
  }

  @HitsLock
  public Question increaseHits(Long questionId) {

    return questionHitService.increase(questionId);
  }
}
