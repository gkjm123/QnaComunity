package com.example.qnacomunity.util;

import static org.junit.jupiter.api.Assertions.*;

import com.example.qnacomunity.dto.form.QuestionForm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeywordUtilTest {

  @Test
  void getKeywords() {
    //given
    QuestionForm form = QuestionForm.builder()
        .title("안녕하세요")
        .content("반갑습니다. 자바를 좋아합니다. 자바는 객체지향 언어입니다. 자바는 자바스크립트와 다릅니다.")
        .keywords("객체지향#언어")
        .reward(5)
        .build();

    //when
    List<String> keywords = KeywordUtil.getKeywords(form);

    //then
    assertEquals(2, keywords.size());
    assertEquals("객체지향", keywords.get(0));
    assertEquals("언어", keywords.get(1));
  }


  @Test
  void getAutoKeyword() {
    //given
    QuestionForm form = QuestionForm.builder()
        .title("안녕하세요")
        .content("반갑습니다. 자바를 좋아합니다. 자바는 객체지향 언어입니다. 자바는 자바스크립트와 다릅니다.")
        .keywords("")
        .reward(5)
        .build();

    //when
    List<String> keywords = KeywordUtil.getKeywords(form);

    //then
    assertEquals(1, keywords.size());
    assertEquals("자바", keywords.get(0));
  }

  @Test
  void getAutoKeywordVerbToNoun() {
    //given
    QuestionForm form = QuestionForm.builder()
        .title("안녕하세요")
        .content("안녕하세요 안녕하세요")
        .keywords("")
        .reward(5)
        .build();

    //when
    List<String> keywords = KeywordUtil.getKeywords(form);

    //then
    assertEquals(1, keywords.size());
    assertEquals("안녕하세요", keywords.get(0));
  }

  @Test
  void getAutoKeywordVerbNotNoun() {
    //given
    QuestionForm form = QuestionForm.builder()
        .title("안녕하세요.")
        .content("안녕하세요. 안녕하세요.")
        .keywords("")
        .reward(5)
        .build();

    //when
    List<String> keywords = KeywordUtil.getKeywords(form);

    //then
    assertEquals(0, keywords.size());
  }
}