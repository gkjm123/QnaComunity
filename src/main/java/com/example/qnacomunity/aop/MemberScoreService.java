package com.example.qnacomunity.aop;

import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.entity.ScoreHistory;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.MemberRepository;
import com.example.qnacomunity.repository.ScoreHistoryRepository;
import com.example.qnacomunity.type.ScoreDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberScoreService {

  private final MemberRepository memberRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;

  //락 획득 후 진행
  @Transactional
  public Member change(Long memberId, int score, ScoreDescription description, Question relatedQuestion) {

    Member member =  memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    //변경 전 스코어
    int previous = member.getScore();

    //변경 후 스코어
    int remain = member.getScore() + score;

    //멤버의 스코어 변경
    member.setScore(remain);
    memberRepository.save(member);

    //스코어 히스토리에 기록
    scoreHistoryRepository.save(ScoreHistory.builder()
        .member(member)
        .score(score)
        .previous(previous)
        .remain(remain)
        .description(description) //변경 사유
        .relatedQuestion(relatedQuestion) //연관 질문(null 가능)
        .build());

    return member;
  }
}
