package com.example.qnacomunity.dto.form;

import com.example.qnacomunity.type.SearchOrder;
import com.example.qnacomunity.type.SearchRange;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SearchForm {

  @Getter
  @Builder
  public static class WordSearchForm {
    @Size(min = 1, max = 20, message = "양식 오류: 검색어를 1~20자 사이로 입력해주세요.")
    String word;

    //검색 정렬(최신/정확도)
    SearchOrder searchOrder;

    //검색 범위(제목/제목+내용)
    SearchRange searchRange;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class KeywordSearchForm {
    @Size(min = 2, max = 20, message = "양식 오류: 키워드 검색어를 2~20자 사이로 입력해주세요.")
    String keyword;
  }
}
