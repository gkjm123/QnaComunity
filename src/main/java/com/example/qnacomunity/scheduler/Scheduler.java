package com.example.qnacomunity.scheduler;

import com.example.qnacomunity.service.ElasticSearchService;
import com.example.qnacomunity.entity.Failure;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.repository.FailureRepository;
import com.example.qnacomunity.type.FailureType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

  private final FailureRepository failureRepository;
  private final ElasticSearchService elasticSearchService;

  private static final int PAGE_SIZE = 10;

  //매 시간 마다 실패 내역 확인후 재시도
  @Scheduled(cron = "${spring.scheduler.time}")
  @Transactional
  public void failurePatch() {

    Pageable pageable = PageRequest.of(0, PAGE_SIZE);

    while (true) {

      List<Failure> failures = failureRepository.findAllBy(pageable);

      if (failures.isEmpty()) {
        break;
      }

      for (Failure failure : failures) {

        try {
          Question question = failure.getQuestion();

          if (failure.getFailureType() == FailureType.SAVE_FAIL) {
            elasticSearchService.save(question);
            log.info("question {}: ES 저장 성공", question.getId());
          }

          else {
            elasticSearchService.delete(question.getId());
            log.info("question {}: ES 삭제 성공", question.getId());
          }

          failureRepository.delete(failure);

        } catch (Exception e) {
          log.error("ES 업데이트 실패: {}", e.getMessage());
        }
      }

      pageable = pageable.next();
    }
  }
}