package com.example.qnacomunity.controller;

import com.example.qnacomunity.service.RankService;
import com.example.qnacomunity.service.RankService.Rank;
import io.swagger.v3.oas.annotations.Operation;
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

  @Operation(summary = "멤버 랭킹 조회")
  @GetMapping("/member")
  public ResponseEntity<List<Rank>> getMemberRank() {
    return ResponseEntity.ok(rankService.getMemberRank());
  }

  @Operation(summary = "키워드 랭킹 조회")
  @GetMapping("/keyword")
  public ResponseEntity<List<Rank>> getKeywordRank() {
    return ResponseEntity.ok(rankService.getKeywordRank());
  }
}
