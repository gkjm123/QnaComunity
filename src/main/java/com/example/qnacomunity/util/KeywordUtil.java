package com.example.qnacomunity.util;

import com.example.qnacomunity.dto.form.QuestionForm;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.util.StringUtils;

public class KeywordUtil {

  public static List<String> getKeywords(QuestionForm form) {

    //사용자가 키워드를 직접 입력했을때
    if (StringUtils.hasText(form.getKeywords())) {

      String[] keywords = form.getKeywords().trim().split("#");

      return Arrays.stream(keywords)
          .filter(StringUtils::hasText)
          .map(k -> StringUtils.trimAllWhitespace(k).toLowerCase())
          .distinct().toList();
    }

    //키워드 미입력시 제목과 내용을 통해 자동 추출
    else {
      return autoCreateKeyword(form.getTitle() + " " + form.getContent());
    }
  }

  public static List<String> autoCreateKeyword(String text) {

    Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
    KomoranResult analyzeResultList = komoran.analyze(text);
    List<Token> tokenList = analyzeResultList.getTokenList();

    Map<String, Integer> keyMap = new HashMap<>();

    for (Token token : tokenList) {

      //명사 또는 외국어 일때 카운트
      if (token.getPos().startsWith("NN") || token.getPos().equals("SL")) {

        //영어 단어일 경우 소문자로
        String key = token.getMorph().toLowerCase();

        keyMap.put(key, keyMap.getOrDefault(key, 0) + 1);
      }
    }

    //맵에서 키워드 뽑기
    return keyMap.entrySet().stream()
        .filter(x -> x.getValue() >= 3) //최소 3번 이상 나오는 단어만 남기기
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //횟수 높은 순으로 정렬
        .limit(3) //상위 3개 가져오기
        .map(Map.Entry::getKey).toList();
  }
}
