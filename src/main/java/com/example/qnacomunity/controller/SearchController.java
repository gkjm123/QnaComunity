package com.example.qnacomunity.controller;

import com.example.qnacomunity.service.ElasticSearchService;
import com.example.qnacomunity.type.SearchOrder;
import com.example.qnacomunity.type.SearchRange;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/search")
@RestController
@RequiredArgsConstructor
public class SearchController {

  private final ElasticSearchService elasticSearchService;

  @Operation(summary = "질문 검색(제목/내용)")
  @GetMapping("/word")
  public ResponseEntity<?> searchWord(
      @PageableDefault Pageable pageable,
      @NotBlank(message = "검색어를 입력해주세요.") @RequestParam String word,
      @NotNull(message = "정렬 순서 미지정") @RequestParam SearchOrder searchOrder,
      @NotNull(message = "검색 범위 미지정") @RequestParam SearchRange searchRange
  ) {

    return ResponseEntity.ok(
        elasticSearchService.searchWord(pageable, word, searchOrder, searchRange)
    );
  }

  @Operation(summary = "질문 검색(키워드)")
  @GetMapping("/keyword")
  public ResponseEntity<?> searchKeyword(
      @PageableDefault Pageable pageable,
      @NotBlank(message = "키워드를 입력해주세요.") @RequestParam String keyword
  ) {

    return ResponseEntity.ok(elasticSearchService.searchKeyword(pageable, keyword));
  }

  @Operation(summary = "특정 질문의 연관글 조회")
  @GetMapping("/related-questions/{questionId}")
  public ResponseEntity<?> getRelatedQuestions(@PathVariable Long questionId) {

    return ResponseEntity.ok(elasticSearchService.getRelatedQuestions(questionId));
  }
}
