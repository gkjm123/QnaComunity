package com.example.qnacomunity.service;

import com.example.qnacomunity.dto.response.Rank;
import com.example.qnacomunity.entity.RedisFailure;
import com.example.qnacomunity.repository.RedisFailureRepository;
import com.example.qnacomunity.type.RankType;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

  private final RedisFailureRepository redisFailureRepository;
  private final RedisTemplate<String, String> redisTemplate;

  private static final int RANK_LIMIT = 10;

  @Transactional
  public void increaseKeywordRank(List<String> keywords) {

    for (String keyword : keywords) {

      try {
        redisTemplate.opsForZSet().incrementScore(RankType.KEYWORD.toString(), keyword, 1);

      } catch (Exception e) {
        log.error("Redis 연동 에러", e);

        redisFailureRepository.save(
            RedisFailure.builder()
                .redisKey(RankType.KEYWORD.toString())
                .redisValue(keyword)
                .score(1)
                .build()
        );
      }
    }
  }

  @Transactional
  public void increaseMemberRank(String nickName, int score) {

    try {
      redisTemplate.opsForZSet().incrementScore(RankType.MEMBER.toString(), nickName, score);

    } catch (Exception e) {
      log.error("Redis 연동 에러", e);

      redisFailureRepository.save(
          RedisFailure.builder()
              .redisKey(RankType.MEMBER.toString())
              .redisValue(nickName)
              .score(score)
              .build()
      );
    }
  }

  public List<Rank> getRank(RankType rankType) {

    List<Rank> rankers;
    Long count = redisTemplate.opsForZSet().size(rankType.toString());

    if (count == null) {
      return Collections.emptyList();
    }

    if (count <= RANK_LIMIT) {
      rankers = redisTemplate.opsForZSet()
          .reverseRangeWithScores(rankType.toString(), 0, -1)
          .stream().map(m -> new Rank(m.getValue(), m.getScore().intValue()))
          .toList();
    } else {

      //마지막 등수의 점수
      Double lastScore = redisTemplate.opsForZSet()
          .reverseRangeWithScores(rankType.toString(), RANK_LIMIT - 1, RANK_LIMIT - 1)
          .stream().findFirst().get().getScore();

      //마지막 등수가 여러명일 경우 그들까지 포함한 총 랭커의 수
      long totalRanker = RANK_LIMIT - 1
          + redisTemplate.opsForZSet()
          .rangeByScore(rankType.toString(), lastScore, lastScore)
          .stream().count();

      //랭커들의 점수가 높은 순서대로 리스트로 받음
      rankers = redisTemplate.opsForZSet()
          .reverseRangeWithScores(rankType.toString(), 0, totalRanker - 1)
          .stream().map(m -> new Rank(m.getValue(), m.getScore().intValue()))
          .toList();
    }

    //등수 세팅(동일 점수일때 동일 등수를 가지도록)
    for (int i = 0; i < rankers.size(); i++) {

      if (i == 0) {
        rankers.get(i).setRank(1);
      } else if (rankers.get(i).getScore() == rankers.get(i - 1).getScore()) {
        rankers.get(i).setRank(rankers.get(i - 1).getRank());
      } else {
        rankers.get(i).setRank(i + 1);
      }
    }
    return rankers;
  }
}
