package com.example.qnacomunity.aop;

import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
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
public class QuestionHitAspect {

  private final RedissonClient redissonClient;

  @Around("@annotation(com.example.qnacomunity.aop.HitsLock) && args(questionId)")
  public Object hitsLock(ProceedingJoinPoint pjp, Long questionId) throws Throwable {

    RLock lock = redissonClient.getLock(questionId.toString());

    try {
      boolean acquireLock = lock.tryLock(30, 30, TimeUnit.SECONDS);

      if (!acquireLock) {
        throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);
      }

      return pjp.proceed();

    } catch (Exception e) {
      throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);

    } finally {
      lock.unlock();
    }
  }
}
