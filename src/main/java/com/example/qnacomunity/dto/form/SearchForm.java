package com.example.qnacomunity.dto.form;

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

    //최신순 = 1, 정확도순 = 0
    int byLatest;

    //제목만 = 1, 제목+내용 = 0
    int byTitleOnly;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class KeywordSearchForm {
    @Size(min = 2, max = 20, message = "양식 오류: 키워드 검색어를 2~20자 사이로 입력해주세요.")
    String keyword;
  }
}
