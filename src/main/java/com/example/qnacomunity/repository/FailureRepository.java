package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Failure;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureRepository extends JpaRepository<Failure, Long> {

  List<Failure> findAllBy(Pageable pageable);

}
