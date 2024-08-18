package com.example.qnacomunity.service;

import com.example.qnacomunity.dto.form.GradeForm;
import com.example.qnacomunity.dto.response.GradeResponse;
import com.example.qnacomunity.entity.Grade;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GradeService {

  private final GradeRepository gradeRepository;

  @CacheEvict(value = "grade", allEntries = true)
  @Transactional
  public GradeResponse createGrade(GradeForm form) {

    //동일한 이름의 등급이 있는지 체크
    if (gradeRepository.findByGradeName(form.getGradeName()).isPresent()) {
      throw new CustomException(ErrorCode.GRADE_NAME_EXIST);
    }

    //동일한 최소 스코어의 등급이 있는지 체크
    if (gradeRepository.findByMinScore(form.getMinScore()).isPresent()) {
      throw new CustomException(ErrorCode.GRADE_SCORE_EXIST);
    }

    Grade grade = Grade.builder()
        .gradeName(form.getGradeName())
        .minScore(form.getMinScore())
        .build();

    return GradeResponse.from(gradeRepository.save(grade));
  }

  @Transactional(readOnly = true)
  public Page<GradeResponse> getAllGrades(Pageable pageable) {
    return gradeRepository.findAll(pageable).map(GradeResponse::from);
  }

  @CacheEvict(value = "grade", allEntries = true)
  @Transactional
  public GradeResponse updateGrade(Long gradeId, GradeForm form) {

    //동일한 이름의 등급이 있는지 체크
    if (gradeRepository.findByGradeName(form.getGradeName())
        .map(it -> !it.getId().equals(gradeId)).orElse(false)
    ) {
      throw new CustomException(ErrorCode.GRADE_NAME_EXIST);
    }

    //동일한 최소 스코어의 등급이 있는지 체크
    if (gradeRepository.findByMinScore(form.getMinScore())
        .map(it -> !it.getId().equals(gradeId)).orElse(false)
    ) {
      throw new CustomException(ErrorCode.GRADE_SCORE_EXIST);
    }

    Grade grade = gradeRepository.findById(gradeId)
        .orElseThrow(() -> new CustomException(ErrorCode.GRADE_NOT_FOUND));

    grade.setGradeName(form.getGradeName());
    grade.setMinScore(form.getMinScore());

    return GradeResponse.from(gradeRepository.save(grade));
  }

  @CacheEvict(value = "grade", allEntries = true)
  @Transactional
  public void deleteGrade(Long gradeId) {

    gradeRepository.deleteById(gradeId);
  }

  @Cacheable(key = "#score", value = "grade")
  @Transactional(readOnly = true)
  public String getMyGrades(int score) {

    return gradeRepository
        .findFirstByMinScoreIsLessThanEqualOrderByMinScoreDesc(score)
        .map(Grade::getGradeName).orElse(null);
  }
}
