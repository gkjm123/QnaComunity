package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.AnswerForm;
import com.example.qnacomunity.dto.form.QuestionForm;
import com.example.qnacomunity.dto.response.AnswerResponse;
import com.example.qnacomunity.dto.response.QuestionResponse;
import com.example.qnacomunity.security.CustomUserDetail;
import com.example.qnacomunity.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

  private final QnaService qnaService;

  @Operation(summary = "질문 등록")
  @PostMapping("/question")
  public ResponseEntity<QuestionResponse> createQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody QuestionForm form
  ) {

    return ResponseEntity.ok(qnaService.createQuestion(userDetail.getMemberResponse(), form));
  }

  @Operation(summary = "특정 질문 조회")
  @GetMapping("/question/{questionId}")
  public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long questionId) {

    return ResponseEntity.ok(qnaService.getQuestion(questionId));
  }

  @Operation(summary = "모든 질문 조회")
  @GetMapping("/questions")
  public ResponseEntity<Page<QuestionResponse>> getQuestions(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getQuestions(pageable));
  }

  @Operation(summary = "나의 질문 조회")
  @GetMapping("/my-questions")
  public ResponseEntity<Page<QuestionResponse>> getMyQuestions(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getMyQuestions(userDetail.getMemberResponse(), pageable));
  }

  @Operation(summary = "질문 수정")
  @PutMapping("/question/{questionId}")
  public ResponseEntity<QuestionResponse> updateQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long questionId,
      @Valid @RequestBody QuestionForm form
  ) {

    return ResponseEntity.ok(
        qnaService.updateQuestion(userDetail.getMemberResponse(), questionId, form));
  }

  @Operation(summary = "질문 삭제")
  @DeleteMapping("/question/{questionId}")
  public ResponseEntity<String> deleteQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long questionId
  ) {

    qnaService.deleteQuestion(userDetail.getMemberResponse(), questionId);
    return ResponseEntity.ok("삭제 완료");
  }

  @Operation(summary = "답변 등록")
  @PostMapping("/answer")
  public ResponseEntity<AnswerResponse> createAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody AnswerForm form
  ) {

    return ResponseEntity.ok(qnaService.createAnswer(userDetail.getMemberResponse(), form));
  }

  @Operation(summary = "특정 답변 조회")
  @GetMapping("/answer/{answerId}")
  public ResponseEntity<AnswerResponse> getAnswer(@PathVariable Long answerId) {

    return ResponseEntity.ok(qnaService.getAnswer(answerId));
  }

  @Operation(summary = "특정 질문의 모든 답변 조회")
  @GetMapping("/answers/{questionId}")
  public ResponseEntity<Page<AnswerResponse>> getAnswers(
      @PathVariable Long questionId,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getAnswers(questionId, pageable));
  }

  @Operation(summary = "나의 답변 조회")
  @GetMapping("/my-answers")
  public ResponseEntity<Page<AnswerResponse>> getMyAnswers(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getMyAnswers(userDetail.getMemberResponse(), pageable));
  }

  @Operation(summary = "답변 수정")
  @PutMapping("/answer/{answerId}")
  public ResponseEntity<AnswerResponse> updateAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId,
      @Valid @RequestBody AnswerForm form
  ) {

    return ResponseEntity.ok(
        qnaService.updateAnswer(userDetail.getMemberResponse(), answerId, form));
  }

  @Operation(summary = "답변 삭제")
  @DeleteMapping("/answer/{answerId}")
  public ResponseEntity<String> deleteAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId
  ) {

    qnaService.deleteAnswer(userDetail.getMemberResponse(), answerId);
    return ResponseEntity.ok("삭제 완료");
  }

  @Operation(summary = "답변 채택")
  @PutMapping("/picked-answer/{answerId}")
  public ResponseEntity<String> pickAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId
  ) {

    qnaService.pickAnswer(userDetail.getMemberResponse(), answerId);
    return ResponseEntity.ok("채택 완료");
  }
}
