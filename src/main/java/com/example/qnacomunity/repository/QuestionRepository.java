package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

  Page<Question> findAllByMember_Id(Long memberId, Pageable pageable);

  Page<Question> findAll(Pageable pageable);

  @Query(value = "SELECT COUNT(*) FROM question WHERE JSON_CONTAINS(keywords->\"$.keywords\",JSON_QUOTE(:key))", nativeQuery = true)
  int countKeywords(@Param("key") String keyword);
}
