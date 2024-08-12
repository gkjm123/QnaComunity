package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.response.Rank;
import com.example.qnacomunity.service.RankService;
import com.example.qnacomunity.type.RankType;
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
  public ResponseEntity<List<Rank>> getMemberRank() {
    return ResponseEntity.ok(rankService.getRank(RankType.MEMBER));
  }

  @GetMapping("/keyword")
  public ResponseEntity<List<Rank>> getKeywordRank() {
    return ResponseEntity.ok(rankService.getRank(RankType.KEYWORD));
  }
}
