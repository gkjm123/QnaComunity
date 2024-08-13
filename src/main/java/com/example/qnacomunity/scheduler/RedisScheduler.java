package com.example.qnacomunity.scheduler;

import com.example.qnacomunity.entity.RedisFailure;
import com.example.qnacomunity.repository.RedisFailureRepository;
import com.example.qnacomunity.type.RankType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisScheduler {

  private static final int PAGE_SIZE = 10;

  private final RedisFailureRepository redisFailureRepository;
  private final RedisTemplate<String, String> redisTemplate;

  //매 시간 마다 실패 내역 확인후 재시도
  @Scheduled(cron = "${spring.scheduler.redis-update}")
  @Transactional
  public void redisFailurePatch() {

    int count = (int) Math.ceil((double) redisFailureRepository.count() / PAGE_SIZE);

    for (int i = 0; i < count; i++) {

      Pageable pageable = PageRequest.of(i, PAGE_SIZE);
      List<RedisFailure> redisFailures = redisFailureRepository.findAllBy(pageable);

      for (RedisFailure redisFailure : redisFailures) {

        try {
          redisTemplate.opsForZSet().incrementScore(
              redisFailure.getRedisKey(),
              redisFailure.getRedisValue(),
              redisFailure.getScore()
          );

          redisFailureRepository.delete(redisFailure);

        } catch (Exception e) {
          log.error("Redis 연동 에러", e);
        }
      }
    }
  }
}