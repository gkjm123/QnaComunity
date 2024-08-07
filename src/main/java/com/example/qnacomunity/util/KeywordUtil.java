package com.example.qnacomunity.util;

import com.example.qnacomunity.dto.form.QuestionForm;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordUtil {

  public static List<String> getKeywords(QuestionForm form) {

    //사용자가 키워드를 직접 입력했을때
    if (!Objects.equals(form.getKeywords(), "")) {

      //# 으로 구분된 문장 -> 공백 구분으로 변경
      String s = form.getKeywords()
          .toLowerCase()
          .replace(" ", "")
          .replace("##", "#")
          .replace("#", " ")
          .trim();

      //중복 제거 후 키워드 리스트 반환
      return Arrays.stream(s.split(" ")).distinct().toList();
    }

    //키워드 미입력시 제목과 내용을 통해 자동 추출
    else {
      return autoCreateKeyword(form.getTitle() + " " + form.getContent());
    }
  }

  public static List<String> autoCreateKeyword(String text) {

    Map<String, Integer> keyMap = new HashMap<>();

    text = text.toLowerCase() //소문자로
        .replace("\n", " ") //특수문자 없애기
        .replace(".", " ")
        .replace("?", " ")
        .replace("!", " ")
        .replace("(", " ")
        .replace(")", " ")
        .replace("{", " ")
        .replace("}", " ")
        .replace(",", "와") //쉼표로 구분된것은 조사를 붙여 필터에 걸리도록(키워드 가능성 높음)
        .replace("  ", " "); //이중 공백 없애기

    String[] words = text.split(" ");

    //조사
    List<String> include =
        List.of("도", "와", "과", "으로", "로", "에서의", "에서", "의", "은", "는", "이", "가", "을", "를");

    //제외
    List<String> exclude =
        List.of("하는", "했는", "되는", "다는", "것은", "것이", "것을", "했을", "것도", "해도", "것과", "것으로", "것의", "것에서");

    for (String word : words) {

      //조사로 끝나고 제외로는 끝나지 않는 단어 찾기
      if (include.stream().anyMatch(word::endsWith) && exclude.stream().noneMatch(word::endsWith)) {

        //조사
        String postPosition = include.stream().filter(word::endsWith).findFirst().get();

        //조사 부분 자르기
        String key = word.substring(0, word.length() - postPosition.length());

        //key 가 2글자 이상이고 맵에 등록된게 없다면
        if (key.length() >= 2 && !keyMap.containsKey(key)) {

          //키가 문단에 포함된 개수 만큼 map 에 저장
          keyMap.put(key, (" " + text + " ").split(key).length - 1);
        }
      }

      //영어 단어인 경우
      else if (Arrays.stream(word.split("")).allMatch(x -> x.charAt(0) >= 97 && x.charAt(0) <= 122)) {

        //맵에 등록된게 없으면 저장
        if (!keyMap.containsKey(word)) {
          keyMap.put(word, (" " + text + " ").split(word).length - 1);
        }
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
