package com.example.qnacomunity.aop;

import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionHitService {
  private final QuestionRepository questionRepository;

  @HitsLock
  public Question increaseHits(Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    question.setHits(question.getHits() + 1);

    return questionRepository.save(question);
  }
}
