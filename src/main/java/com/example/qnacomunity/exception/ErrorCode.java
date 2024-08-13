package com.example.qnacomunity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
  Q_NOT_FOUND("질문을 찾을 수 없습니다."),
  Q_MEMBER_NOT_MATCH("질문자가 아닙니다."),
  Q_ANSWERED("답변이 달린 질문은 수정, 삭제 할 수 없습니다."),
  Q_PICKED("이미 채택 완료된 질문입니다."),
  Q_IS_YOURS("자신의 질문에 답변할수 없습니다."),
  A_NOT_FOUND("답변을 찾을 수 없습니다."),
  A_MEMBER_NOT_MATCH("답변자가 아닙니다."),
  A_PICKED("채택된 답변은 수정, 삭제 할 수 없습니다."),
  LOGIN_FAIL("아이디, 비밀번호를 확인해주세요."),
  PASS_CHECK_FAIL("비밀번호 확인이 일치하지 않습니다."),
  PASS_NOT_MATCH("이전 비밀번호가 일치하지 않습니다."),
  ID_NOT_FOUND("아이디를 찾을 수 없습니다."),
  ID_EXIST("이미 존재하는 아이디입니다."),
  GRADE_EXIST("이름이나 최소 스코어가 동일한 등급이 있습니다."),
  GRADE_NOT_FOUND("등급을 찾을수 없습니다."),
  SCORE_NOT_ENOUGH("가지고 있는 스코어보다 보상을 많이 걸 수 없습니다."),
  RANK_NOT_FOUND("랭킹 정보 확인 불가"),
  RANK_UPDATE_FAIL("랭킹 업데이트 실패"),
  ACQUIRE_LOCK_FAIL("락 획득 실패"),
  FAILURE_TYPE_ERROR("연동 실패 타입 확인 불가");

  private final String message;
}
