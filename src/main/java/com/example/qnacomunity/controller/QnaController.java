package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.AnswerForm;
import com.example.qnacomunity.dto.form.QuestionForm;
import com.example.qnacomunity.dto.response.AnswerResponse;
import com.example.qnacomunity.dto.response.QuestionResponse;
import com.example.qnacomunity.security.CustomUserDetail;
import com.example.qnacomunity.service.QnaService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

  @PostMapping("/question")
  public ResponseEntity<QuestionResponse> createQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody QuestionForm form
  ) {

    return ResponseEntity.ok(qnaService.createQuestion(userDetail.getMemberResponse(), form));
  }

  @GetMapping("/question/{questionId}")
  public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long questionId) {

    return ResponseEntity.ok(qnaService.getQuestion(questionId));
  }

  @GetMapping("/questions")
  public ResponseEntity<Page<QuestionResponse>> getQuestions(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getQuestions(pageable));
  }

  @GetMapping("/my-questions")
  public ResponseEntity<Page<QuestionResponse>> getMyQuestions(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getMyQuestions(userDetail.getMemberResponse(), pageable));
  }

  @PutMapping("/question/{questionId}")
  public ResponseEntity<QuestionResponse> updateQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long questionId,
      @Valid @RequestBody QuestionForm form
  ) {

    return ResponseEntity.ok(
        qnaService.updateQuestion(userDetail.getMemberResponse(), questionId, form));
  }

  @DeleteMapping("/question/{questionId}")
  public ResponseEntity<String> deleteQuestion(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long questionId
  ) {

    qnaService.deleteQuestion(userDetail.getMemberResponse(), questionId);
    return ResponseEntity.ok("삭제 완료");
  }

  @PostMapping("/answer")
  public ResponseEntity<AnswerResponse> createAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @Valid @RequestBody AnswerForm form
  ) {

    return ResponseEntity.ok(qnaService.createAnswer(userDetail.getMemberResponse(), form));
  }

  @GetMapping("/answer/{answerId}")
  public ResponseEntity<AnswerResponse> getAnswer(@PathVariable Long answerId) {

    return ResponseEntity.ok(qnaService.getAnswer(answerId));
  }

  @GetMapping("/answers/{questionId}")
  public ResponseEntity<Page<AnswerResponse>> getAnswers(
      @PathVariable Long questionId,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getAnswers(questionId, pageable));
  }

  @GetMapping("/my-answers")
  public ResponseEntity<Page<AnswerResponse>> getMyAnswers(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(qnaService.getMyAnswers(userDetail.getMemberResponse(), pageable));
  }

  @PutMapping("/answer/{answerId}")
  public ResponseEntity<AnswerResponse> updateAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId,
      @Valid @RequestBody AnswerForm form
  ) {

    return ResponseEntity.ok(
        qnaService.updateAnswer(userDetail.getMemberResponse(), answerId, form));
  }

  @DeleteMapping("/answer/{answerId}")
  public ResponseEntity<String> deleteAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId
  ) {

    qnaService.deleteAnswer(userDetail.getMemberResponse(), answerId);
    return ResponseEntity.ok("삭제 완료");
  }

  @PutMapping("/picked-answer/{answerId}")
  public ResponseEntity<String> pickAnswer(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long answerId
  ) {

    qnaService.pickAnswer(userDetail.getMemberResponse(), answerId);
    return ResponseEntity.ok("채택 완료");
  }
}
