package com.example.qnacomunity.aop;

import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.type.ScoreDescription;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@org.aspectj.lang.annotation.Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MemberScoreAspect {

  private final RedissonClient redissonClient;

  @Around(
      value = "@annotation(com.example.qnacomunity.aop.ScoreLock) && args(memberId,score,description,relatedQuestion)",
      argNames = "pjp,memberId,score,description,relatedQuestion"
  )
  public Object scoreLock(
      ProceedingJoinPoint pjp,
      Long memberId,
      int score, ScoreDescription description, Question relatedQuestion) throws Throwable {

    RLock lock = redissonClient.getLock(memberId.toString());

    try {
      boolean acquireLock = lock.tryLock(3, 1, TimeUnit.SECONDS);

      if (!acquireLock) {
        throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);
      }

      //LockService.changeScore()
      return pjp.proceed();

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);

    } finally {
      lock.unlock();
    }
  }
}
