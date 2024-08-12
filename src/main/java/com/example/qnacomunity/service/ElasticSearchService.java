package com.example.qnacomunity.service;


import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.qnacomunity.dto.form.SearchForm;
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
import com.example.qnacomunity.type.SearchOrder;
import com.example.qnacomunity.type.SearchRange;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
  private final ElasticFailureRepository elasticFailureRepository;

  public void save(Question question) {

    try {
      QuestionDocument questionDocument = QuestionDocument.from(question);
      elasticSearchRepository.save(questionDocument);

    } catch (Exception e) {
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
      elasticFailureRepository.save(
          ElasticFailure.builder()
              .question(question)
              .elasticFailureType(ElasticFailureType.DELETE_FAIL)
              .build()
      );
    }
  }

  @Transactional
  public List<QuestionResponse> searchWord(Pageable pageable, SearchForm.WordSearchForm form) {

    String word = form.getWord().toLowerCase().replace("  ", " ").trim();

    Query query;
    NativeQuery nativeQuery;

    //제목만 검색
    if (form.getSearchRange() == SearchRange.TITLE) {
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

    SortOptions sortOptions = new SortOptions.Builder()
        .field(f -> f.field("created").order(SortOrder.Desc)).build();

    //최신순 검색
    if (form.getSearchOrder() == SearchOrder.CREATION_TIME) {
      nativeQuery = new NativeQueryBuilder()
          .withQuery(query)
          .withSort(sortOptions)
          .withPageable(pageable)
          .build();
    }

    //정확도순 검색
    else {
      nativeQuery = new NativeQueryBuilder()
          .withQuery(query)
          .withPageable(pageable)
          .build();
    }

    SearchHits<QuestionDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, QuestionDocument.class);

    return searchHits.get().map(s -> QuestionResponse.from(fromDocument(s.getContent()))).toList();
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

    SortOptions sortOptions = new SortOptions.Builder()
        .field(f -> f.field("created").order(SortOrder.Desc)).build();

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(matchQuery)
        .withSort(sortOptions)
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

    String keyword = String.join(" ", question.getKeywords());

    //키워드가 하나 이상 일치하는 글 검색
    Query matchQuery = QueryBuilders.match()
        .query(keyword)
        .field("keywords")
        .build()._toQuery();

    //결과에 질문글 자신이 들어있으면 제외해야 하므로 3+1 개를 받음
    PageRequest pageRequest = PageRequest.of(0, 4);

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(matchQuery)
        .withPageable(pageRequest)
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
