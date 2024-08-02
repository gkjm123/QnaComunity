package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.ScoreHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {

  Page<ScoreHistory> findAllByMember_Id(Long memberId, Pageable pageable);

}
