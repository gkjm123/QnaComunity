package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Question;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

  Page<Question> findAllByMember_Id(Long memberId, Pageable pageable);

  Page<Question> findAll(Pageable pageable);
}
