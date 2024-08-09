package com.example.qnacomunity.scheduler;

import com.example.qnacomunity.service.ElasticSearchService;
import com.example.qnacomunity.entity.Failure;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.repository.FailureRepository;
import com.example.qnacomunity.type.FailureType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

  private final FailureRepository failureRepository;
  private final ElasticSearchService elasticSearchService;

  //매 시간 마다 실패 내역 확인후 재시도
  @Scheduled(cron = "${spring.scheduler.time}")
  @Transactional
  public void failurePatch() {
    List<Failure> failures = failureRepository.findAll();

    for (Failure failure : failures) {

      Question question = failure.getQuestion();

      if (failure.getFailureType() == FailureType.SAVE_FAIL) {
        try {
          elasticSearchService.save(question);
          failureRepository.delete(failure);
          log.info("ES 저장 성공");
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }

      else if (failure.getFailureType() == FailureType.DELETE_FAIL) {
        try {
          elasticSearchService.delete(question.getId());
          failureRepository.delete(failure);
          log.info("ES 삭제 성공");
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    }
  }
}