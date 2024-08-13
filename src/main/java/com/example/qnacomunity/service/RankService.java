package com.example.qnacomunity.service;

import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.MemberRepository;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

  private final MemberRepository memberRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final int RANK_LIMIT = 10;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RankedMember {

    private String name;
    private int score;
  }

  public void updateMemberRank(int scoreBefore, int scoreAfter) {

    String redisMinScore = redisTemplate.opsForValue().get("minScore");

    //번경 전,후 스코어가 랭커 최소 스코어보다 작으면 랭킹에 변동 주지 않으므로 종료
    if (StringUtils.hasText(redisMinScore)
        && Integer.parseInt(redisMinScore) > scoreBefore
        && Integer.parseInt(redisMinScore) > scoreAfter
    ) {
      return;
    }
    updateMemberRank();
  }

  public List<RankedMember> updateMemberRank() {

    List<Member> members = memberRepository.findFirst10ByOrderByScoreDesc();
    List<RankedMember> rankedMembers = members.stream()
        .map(m -> new RankedMember(m.getNickName(), m.getScore())).toList();

    int minScore;

    if (rankedMembers.size() < RANK_LIMIT) {
      minScore = 0;
    } else {
      minScore = rankedMembers.get(RANK_LIMIT - 1).getScore();
    }

    try {
      String rank = mapper.writeValueAsString(rankedMembers);

      redisTemplate.opsForValue().set("memberRank", rank);
      redisTemplate.opsForValue().set("minScore", Integer.toString(minScore));

    } catch (JsonProcessingException e) {
      log.error("멤버 랭크 업데이트 오류", e);
      throw new CustomException(ErrorCode.RANK_UPDATE_FAIL);
    }

    return rankedMembers;
  }

  public List<RankedMember> getMemberRank() {

    String redisMinScore = redisTemplate.opsForValue().get("minScore");

    if (!StringUtils.hasText(redisMinScore)) {
      return updateMemberRank();
    }

    String rank = redisTemplate.opsForValue().get("memberRank");

    try {
      return mapper.readValue(rank, new TypeReference<>() {
      });

    } catch (JacksonException e) {
      log.error("멤버 랭크 확인 오류", e);
      throw new CustomException(ErrorCode.RANK_NOT_FOUND);
    }
  }
}
