package com.example.qnacomunity.controller;

import com.example.qnacomunity.service.RankService;
import com.example.qnacomunity.service.RankService.RankedMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankController {

  private final RankService rankService;

  @GetMapping("/member")
  public ResponseEntity<List<RankedMember>> getMemberRank() {
    return ResponseEntity.ok(rankService.getMemberRank());
  }
}
