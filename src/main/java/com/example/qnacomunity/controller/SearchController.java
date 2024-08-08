package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.SearchForm;
import com.example.qnacomunity.service.ElasticSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/search")
@RestController
@RequiredArgsConstructor
public class SearchController {

  private final ElasticSearchService elasticSearchService;

  //제목 또는 내용 검색
  @GetMapping("/word")
  public ResponseEntity<?> searchWord(
      @PageableDefault Pageable pageable,
      @Valid @RequestBody SearchForm.WordSearchForm form
  ) {

    return ResponseEntity.ok(elasticSearchService.searchWord(pageable, form));
  }

  //키워드 검색
  @GetMapping("/keyword")
  public ResponseEntity<?> searchKeyword(
      @PageableDefault Pageable pageable,
      @Valid @RequestBody SearchForm.KeywordSearchForm form
  ) {

    return ResponseEntity.ok(elasticSearchService.searchKeyword(pageable, form));
  }

  @GetMapping("/related-questions/{questionId}")
  public ResponseEntity<?> getRelatedQuestions(@PathVariable Long questionId) {

    return ResponseEntity.ok(elasticSearchService.getRelatedQuestions(questionId));
  }
}
