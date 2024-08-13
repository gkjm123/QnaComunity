package com.example.qnacomunity.repository;


import com.example.qnacomunity.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByLoginId(String loginId);

  List<Member> findFirst10ByOrderByScoreDesc();

}
