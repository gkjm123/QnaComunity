package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Grade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

  Optional<Grade> findByGradeName(String gradeName);

  Optional<Grade> findByMinScore(int minScore);

  Optional<Grade> findFirstByMinScoreIsLessThanEqualOrderByMinScoreDesc(int memberScore);

}
