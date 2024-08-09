package com.example.qnacomunity.repository;

import com.example.qnacomunity.entity.Failure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureRepository extends JpaRepository<Failure, Long> {

}
