package com.example.qnacomunity.scheduler;

import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
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

    int count = (int) Math.ceil((double) failureRepository.count() / PAGE_SIZE);

    for (int i = 0; i < count; i++) {

      Pageable pageable = PageRequest.of(i, PAGE_SIZE);
      List<Failure> failures = failureRepository.findAllBy(pageable);

      for (Failure failure : failures) {

        try {
          Question question = failure.getQuestion();

          switch(failure.getFailureType()) {

            case SAVE_FAIL:
              elasticSearchService.save(question);
              log.info("question {}: ES 저장 성공", question.getId());
              break;

            case DELETE_FAIL:
              elasticSearchService.delete(question.getId());
              log.info("question {}: ES 삭제 성공", question.getId());
              break;

            default:
              throw new CustomException(ErrorCode.FAILURE_TYPE_ERROR);
          }

          failureRepository.delete(failure);

        } catch (Exception e) {
          log.error("ES 연동 실패", e);
        }
      }
    }
  }
}