package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.ElasticFailure;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticFailureRepository extends JpaRepository<ElasticFailure, Long> {

  List<ElasticFailure> findAllBy(Pageable pageable);

}
