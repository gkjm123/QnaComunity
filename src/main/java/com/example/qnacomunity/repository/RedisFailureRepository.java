package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.RedisFailure;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFailureRepository extends JpaRepository<RedisFailure, Long> {

  List<RedisFailure> findAllBy(Pageable pageable);

}
