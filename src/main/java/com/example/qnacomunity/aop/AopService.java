package com.example.qnacomunity.aop;

import com.example.qnacomunity.entity.Question;
import org.springframework.stereotype.Component;

@Component
public class AopService {

  @HitsLock
  public void increaseHits(Question question) {

    question.setHits(question.getHits() + 1);
  }
}
