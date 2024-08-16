package com.example.qnacomunity.controller;

import com.example.qnacomunity.dto.form.GradeForm;
import com.example.qnacomunity.dto.response.GradeResponse;
import com.example.qnacomunity.security.CustomUserDetail;
import com.example.qnacomunity.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

  private final GradeService gradeService;

  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping
  public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody GradeForm form) {

    return ResponseEntity.ok(gradeService.createGrade(form));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/{gradeId}")
  public ResponseEntity<GradeResponse> updateGrade(
      @PathVariable Long gradeId,
      @Valid @RequestBody GradeForm form
  ) {

    return ResponseEntity.ok(gradeService.updateGrade(gradeId, form));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @DeleteMapping("/{gradeId}")
  public ResponseEntity<String> deleteGrade(@PathVariable Long gradeId) {

    gradeService.deleteGrade(gradeId);
    return ResponseEntity.ok("삭제 완료");
  }

  @GetMapping("/all-grades")
  public ResponseEntity<Page<GradeResponse>> getAllGrades(
      @PageableDefault(sort = "minScore", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    return ResponseEntity.ok(gradeService.getAllGrades(pageable));
  }

  @GetMapping("/my-grade")
  public ResponseEntity<GradeResponse> getMyGrade(
      @AuthenticationPrincipal CustomUserDetail userDetail
  ) {

    return ResponseEntity.ok(gradeService.getMyGrades(userDetail.getMemberResponse()));
  }
}
