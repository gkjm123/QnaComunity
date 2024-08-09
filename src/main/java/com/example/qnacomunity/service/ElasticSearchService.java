package com.example.qnacomunity.service;


import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.qnacomunity.dto.form.SearchForm;
import com.example.qnacomunity.dto.response.QuestionResponse;
import com.example.qnacomunity.elasticsearch.ElasticSearchRepository;
import com.example.qnacomunity.elasticsearch.QuestionDocument;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.QuestionRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

  private final QuestionRepository questionRepository;
  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticSearchRepository elasticSearchRepository;

  @Transactional
  public void save(Question question) {
    QuestionDocument questionDocument = QuestionDocument.from(question);
    elasticSearchRepository.save(questionDocument);
  }

  @Transactional
  public void delete(Long id) {
    elasticSearchRepository.deleteById(id);
  }

  @Transactional
  public List<QuestionResponse> searchWord(Pageable pageable, SearchForm.WordSearchForm form) {

    String word = form.getWord().toLowerCase().replace("  ", " ").trim();
    NativeQuery nativeQuery;

    //제목만 검색
    if (form.getByTitleOnly() == 1) {

      Query query = QueryBuilders.match()
          .query(word)
          .field("title")
          .build()._toQuery();

      nativeQuery = new NativeQueryBuilder()
          .withQuery(query)
          .build();
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

      Query query = QueryBuilders.bool()
          .should(matchTitle, matchContent)
          .build()._toQuery();

      nativeQuery = new NativeQueryBuilder()
          .withQuery(query)
          .build();
    }

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    //최신순
    if (form.getByLatest() == 1) {
      return searchHits.get()
          .map(s -> QuestionResponse.from(fromDocument(s.getContent())))
          .sorted(Comparator.comparing(QuestionResponse::getCreatedAt, Comparator.reverseOrder()))
          .skip((long) pageable.getPageNumber() * pageable.getPageSize())
          .limit(pageable.getPageSize())
          .toList();
    }

    //정확도순
    else {
      return searchHits.get()
          .map(s -> QuestionResponse.from(fromDocument(s.getContent())))
          .skip((long) pageable.getPageNumber() * pageable.getPageSize())
          .limit(pageable.getPageSize())
          .toList();
    }
  }

  @Transactional
  public List<QuestionResponse> searchKeyword(Pageable pageable, SearchForm.KeywordSearchForm form) {

    String keyword = form.getKeyword().toLowerCase()
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
        .build();

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    //최신순 정렬
    return searchHits.get()
        .map(s -> QuestionResponse.from(fromDocument(s.getContent())))
        .sorted(Comparator.comparing(QuestionResponse::getCreatedAt, Comparator.reverseOrder()))
        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
        .limit(pageable.getPageSize())
        .toList();
  }

  @Transactional
  public List<QuestionResponse> getRelatedQuestions(Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    String keyword = String.join(" ", question.getKeywords());

    //키워드가 하나 이상 일치하는 글 검색
    Query matchQuery = QueryBuilders.match()
        .query(keyword)
        .field("keywords")
        .build()._toQuery();

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(matchQuery)
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
