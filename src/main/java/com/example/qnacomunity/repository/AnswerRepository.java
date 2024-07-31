package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

  Page<Answer> findAllByQuestion_Id(Long questionId, Pageable pageable);

  Page<Answer> findAllByMember_Id(Long memberId, Pageable pageable);

  boolean existsByQuestion_Id(Long questionId);

  boolean existsByQuestion_IdAndPickedAtIsNotNull(Long questionId);
}
