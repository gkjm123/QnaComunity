package com.example.qnacomunity.aop;

import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@org.aspectj.lang.annotation.Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAspect {

  private final RedissonClient redissonClient;

  @Around("@annotation(com.example.qnacomunity.aop.AopLock)")
  public Object lockAspect(ProceedingJoinPoint pjp) throws Throwable {

    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    AopLock aopLock = method.getAnnotation(AopLock.class);

    String key = aopLock.type() +
        CustomExpressionParser.getValue(signature.getParameterNames(), pjp.getArgs(), aopLock.key());

    RLock lock = redissonClient.getLock(key);

    try {
      boolean acquireLock = lock.tryLock(3, 1, TimeUnit.SECONDS);

      if (!acquireLock) {
        log.error("락 획득 실패");
        throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);
      }

      return pjp.proceed();

    } catch (Exception e) {
      log.error("락 획득 실패: {}", e.getMessage());
      throw new CustomException(ErrorCode.ACQUIRE_LOCK_FAIL);

    } finally {
      lock.unlock();
    }
  }
}