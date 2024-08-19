package com.example.qnacomunity.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.qnacomunity.dto.response.QuestionResponse;
import com.example.qnacomunity.elasticsearch.ElasticSearchRepository;
import com.example.qnacomunity.elasticsearch.QuestionDocument;
import com.example.qnacomunity.entity.ElasticFailure;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.ElasticFailureRepository;
import com.example.qnacomunity.repository.QuestionRepository;
import com.example.qnacomunity.type.ElasticFailureType;
import com.example.qnacomunity.type.SearchRange;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {

  private final QuestionRepository questionRepository;
  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticSearchRepository elasticSearchRepository;
  private final ElasticFailureRepository elasticFailureRepository;

  public void save(Question question) {

    try {
      QuestionDocument questionDocument = QuestionDocument.from(question);
      elasticSearchRepository.save(questionDocument);

    } catch (Exception e) {
      log.error("ES 연동 에러", e);

      elasticFailureRepository.save(
          ElasticFailure.builder()
              .question(question)
              .elasticFailureType(ElasticFailureType.SAVE_FAIL)
              .build()
      );
    }
  }

  public void delete(Question question) {

    try {
      elasticSearchRepository.deleteById(question.getId());

    } catch (Exception e) {
      log.error("ES 연동 에러", e);

      elasticFailureRepository.save(
          ElasticFailure.builder()
              .question(question)
              .elasticFailureType(ElasticFailureType.DELETE_FAIL)
              .build()
      );
    }
  }

  @Transactional
  public List<QuestionResponse> searchWord(
      Pageable pageable,
      String word,
      SearchRange searchRange
  ) {

    word = word.toLowerCase().replace("  ", " ").trim();

    Query query;
    NativeQuery nativeQuery;

    //제목만 검색
    if (searchRange == SearchRange.TITLE) {
      query = QueryBuilders.match()
          .query(word)
          .field("title")
          .build()._toQuery();
    }

    //제목+내용 검색
    else {
      Query matchTitle = QueryBuilders.match()
          .query(word)
          .field("title")
          .build()._toQuery();

      Query matchContent = QueryBuilders.match()
          .query(word)
          .field("content")
          .build()._toQuery();

      query = QueryBuilders.bool()
          .should(matchTitle, matchContent)
          .build()._toQuery();
    }

    nativeQuery = new NativeQueryBuilder()
        .withQuery(query)
        .withPageable(pageable)
        .build();

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    return searchHits.get().map(s -> QuestionResponse.from(fromDocument(s.getContent()))).toList();
  }

  @Transactional
  public List<QuestionResponse> searchKeyword(Pageable pageable, String keyword) {

    keyword = keyword.toLowerCase()
        .replace(" ", "")
        .replace("##", "#")
        .replace("#", " ")
        .trim();

    //키워드 모두 일치하는 글 조회
    Query matchQuery = QueryBuilders.match()
        .query(keyword)
        .field("keywords")
        .operator(Operator.And)
        .build()._toQuery();

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(matchQuery)
        .withPageable(pageable)
        .build();

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    return searchHits.get().map(s -> QuestionResponse.from(fromDocument(s.getContent()))).toList();
  }

  @Transactional
  public List<QuestionResponse> getRelatedQuestions(Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    String keyword = String.join(" ", question.getKeywords().get("keywords"));

    //키워드가 하나 이상 일치하는 글 검색
    Query matchQuery = QueryBuilders.match()
        .query(keyword)
        .field("keywords")
        .build()._toQuery();

    //결과에 질문글 자신이 들어있으면 제외해야 하므로 3+1 개를 받음
    Pageable pageable = PageRequest.of(0, 4);

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(matchQuery)
        .withPageable(pageable)
        .build();

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    return searchHits.get()
        .map(s -> QuestionResponse.from(fromDocument(s.getContent())))
        .filter(q -> !q.getId().equals(questionId)) //연관글에서 자기 자신 제거
        .limit(3)
        .toList();
  }

  private Question fromDocument(QuestionDocument document) {
    return questionRepository.findById(document.getId()).orElse(null);
  }
}
