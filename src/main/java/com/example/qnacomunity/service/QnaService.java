package com.example.qnacomunity.service;

import com.example.qnacomunity.aop.MemberScoreService;
import com.example.qnacomunity.aop.QuestionHitService;
import com.example.qnacomunity.dto.form.AnswerForm;
import com.example.qnacomunity.dto.form.QuestionForm;
import com.example.qnacomunity.dto.response.AnswerResponse;
import com.example.qnacomunity.dto.response.MemberResponse;
import com.example.qnacomunity.dto.response.QuestionResponse;
import com.example.qnacomunity.entity.Answer;
import com.example.qnacomunity.entity.Member;
import com.example.qnacomunity.entity.Question;
import com.example.qnacomunity.exception.CustomException;
import com.example.qnacomunity.exception.ErrorCode;
import com.example.qnacomunity.repository.AnswerRepository;
import com.example.qnacomunity.repository.QuestionRepository;
import com.example.qnacomunity.type.ScoreChangeType;
import com.example.qnacomunity.type.ScoreDescription;
import com.example.qnacomunity.util.KeywordUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QnaService {

  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final MemberService memberService;
  private final MemberScoreService memberScoreService;
  private final QuestionHitService questionHitService;
  private final ElasticSearchService elasticSearchService;

  private static final int PAYBACK_SCORE = 5;
  private final RankService rankService;

  public QuestionResponse createQuestion(MemberResponse memberResponse, QuestionForm form) {

    //질문자가 보상 스코어 보다 많이 가지고 있는지 체크
    if (memberResponse.getScore() < form.getReward()) {
      throw new CustomException(ErrorCode.SCORE_NOT_ENOUGH);
    }

    Question question = questionRepository.save(
        Question.builder()
            .title(form.getTitle())
            .content(form.getContent())
            .reward(form.getReward())
            .build()
    );

    //보상 스코어 만큼 질문자에게 차감
    Member member = memberScoreService.change(
        memberResponse.getId(), //질문자 ID
        ScoreChangeType.MINUS,
        form.getReward(),  //변경 스코어
        ScoreDescription.QUESTION_MADE, //변경 사유
        question //연관 질문(null 가능)
    );

    //질문에 멤버 세팅
    question.setMember(member);

    //질문에 키워드 세팅
    setKeywords(question, form);

    //elasticSearch 저장, 실패시 Failure 테이블에 실패 내역 남기기
    elasticSearchService.save(question);

    //멤버 랭크 업데이트
    rankService.updateMemberRank(
        memberResponse.getScore(), //변경 전 스코어
        memberResponse.getScore() - form.getReward() //변경 후 스코어
    );

    return QuestionResponse.from(questionRepository.save(question));
  }

  public QuestionResponse getQuestion(Long questionId) {

    //조회수 증가
    Question question = questionHitService.increase(questionId);

    return QuestionResponse.from(question);
  }

  @Transactional(readOnly = true)
  public Page<QuestionResponse> getQuestions(Pageable pageable) {

    //Questions 조회, Page 반환
    Page<Question> questions = questionRepository.findAll(pageable);

    return questions.map(QuestionResponse::from);
  }

  @Transactional(readOnly = true)
  public Page<QuestionResponse> getMyQuestions(MemberResponse memberResponse, Pageable pageable) {

    //해당 멤버가 생성한 Questions 최신순으로 반환
    Page<Question> questions =
        questionRepository.findAllByMember_Id(memberResponse.getId(), pageable);

    return questions.map(QuestionResponse::from);
  }

  public QuestionResponse updateQuestion(
      MemberResponse memberResponse,
      Long questionId,
      QuestionForm form
  ) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    //질문자 본인 맞는지 체크
    if (!Objects.equals(question.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.Q_MEMBER_NOT_MATCH);
    }

    //질문 달린 답변은 수정 불가
    if (answerRepository.existsByQuestion_Id(questionId)) {
      throw new CustomException(ErrorCode.Q_ANSWERED);
    }

    //질문자가 추가할 보상 스코어 보다 많이 가지고 있는지 체크
    if (memberResponse.getScore() < form.getReward() - question.getReward()) {
      throw new CustomException(ErrorCode.SCORE_NOT_ENOUGH);
    }

    //변경된 보상 스코어 만큼 멤버에게 스코어 증감 또는 차감
    if (question.getReward() != form.getReward()) {
      memberScoreService.change(
          memberResponse.getId(),
          question.getReward() - form.getReward() > 0 ? ScoreChangeType.PLUS
              : ScoreChangeType.MINUS,
          Math.abs(question.getReward() - form.getReward()),
          ScoreDescription.QUESTION_CHANGE,
          question
      );
    }

    //질문에 키워드 세팅
    setKeywords(question, form);

    //Elasticsearch 도큐먼트 업데이트
    elasticSearchService.save(question);

    //멤버 랭크 업데이트
    rankService.updateMemberRank(
        memberResponse.getScore(), //변경 전 스코어
        memberResponse.getScore() + question.getReward() - form.getReward() //변경 후 스코어
    );

    question.setTitle(form.getTitle());
    question.setContent(form.getContent());
    question.setReward(form.getReward());

    return QuestionResponse.from(questionRepository.save(question));
  }

  public void deleteQuestion(MemberResponse memberResponse, Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    //질문자 본인 맞는지 체크
    if (!Objects.equals(question.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.Q_MEMBER_NOT_MATCH);
    }

    //질문 달린 답변은 삭제 불가
    if (answerRepository.existsByQuestion_Id(questionId)) {
      throw new CustomException(ErrorCode.Q_ANSWERED);
    }

    //질문자에게 보상 스코어 반환
    memberScoreService.change(
        memberResponse.getId(),
        ScoreChangeType.PLUS,
        question.getReward(),
        ScoreDescription.QUESTION_DELETE,
        question
    );

    //멤버 랭크 업데이트
    rankService.updateMemberRank(
        memberResponse.getScore(), //변경 전 스코어
        memberResponse.getScore() + question.getReward() //변경 후 스코어
    );

    elasticSearchService.delete(question);
  }

  @Transactional
  public AnswerResponse createAnswer(MemberResponse memberResponse, AnswerForm form) {

    Member member = memberService.getMember(memberResponse);

    Question question = questionRepository.findById(form.getQuestionId())
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    //질문자와 답변자가 동일한지 체크(본인 질문에 답변 불가)
    if (question.getMember().getId().equals(member.getId())) {
      throw new CustomException(ErrorCode.Q_IS_YOURS);
    }

    Answer answer = Answer.builder()
        .member(member)
        .question(question)
        .content(form.getContent())
        .build();

    return AnswerResponse.from(answerRepository.save(answer));
  }

  @Transactional(readOnly = true)
  public AnswerResponse getAnswer(Long answerId) {

    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new CustomException(ErrorCode.A_NOT_FOUND));

    return AnswerResponse.from(answer);
  }

  @Transactional(readOnly = true)
  public Page<AnswerResponse> getAnswers(Long questionId, Pageable pageable) {

    //해당 질문에 달린 답변 반환
    Page<Answer> answers = answerRepository.findAllByQuestion_Id(questionId, pageable);

    return answers.map(AnswerResponse::from);
  }

  @Transactional(readOnly = true)
  public Page<AnswerResponse> getMyAnswers(MemberResponse memberResponse, Pageable pageable) {

    //해당 멤버가 작성한 답변 반환
    Page<Answer> answers = answerRepository
        .findAllByMember_Id(memberResponse.getId(), pageable);

    return answers.map(AnswerResponse::from);
  }

  @Transactional
  public AnswerResponse updateAnswer(
      MemberResponse memberResponse,
      Long answerId,
      AnswerForm form
  ) {

    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new CustomException(ErrorCode.A_NOT_FOUND));

    //답변 작성자 본인 체크
    if (!Objects.equals(answer.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.A_MEMBER_NOT_MATCH);
    }

    //답변 채택 여부 체크(채택된 답변은 수정 불가)
    if (answer.getPickedAt() != null) {
      throw new CustomException(ErrorCode.A_PICKED);
    }

    //답변 내용 수정
    answer.setContent(form.getContent());

    return AnswerResponse.from(answerRepository.save(answer));
  }

  @Transactional
  public void deleteAnswer(MemberResponse memberResponse, Long answerId) {

    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new CustomException(ErrorCode.A_NOT_FOUND));

    //답변 작성자 본인 체크
    if (!Objects.equals(answer.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.A_MEMBER_NOT_MATCH);
    }

    //답변 채택 여부 체크(채택된 답변은 삭제 불가)
    if (answer.getPickedAt() != null) {
      throw new CustomException(ErrorCode.A_PICKED);
    }

    answerRepository.deleteById(answerId);
  }

  public void pickAnswer(MemberResponse memberResponse, Long answerId) {

    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new CustomException(ErrorCode.A_NOT_FOUND));

    Question question = answer.getQuestion();

    //질문자 본인 체크
    if (!Objects.equals(question.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.Q_MEMBER_NOT_MATCH);
    }

    //질문에 채택된 답변이 있으면 다시 채택 불가
    if (answerRepository.existsByQuestion_IdAndPickedAtIsNotNull(question.getId())) {
      throw new CustomException(ErrorCode.Q_PICKED);
    }

    //질문자에게 채택 페이백 스코어(5점) 제공
    memberScoreService.change(
        memberResponse.getId(),
        ScoreChangeType.PLUS,
        PAYBACK_SCORE,
        ScoreDescription.PICK_PAYBACK,
        question
    );

    //답변자에게 보상 스코어 제공
    memberScoreService.change(
        answer.getMember().getId(),
        ScoreChangeType.PLUS,
        question.getReward(),
        ScoreDescription.ANSWER_PICKED,
        question
    );

    //멤버 랭크 업데이트
    rankService.updateMemberRank(
        Math.max(memberResponse.getScore(), answer.getMember().getScore()), //변경 전 스코어(질문자,답변자 중 큰쪽)
        Math.max(memberResponse.getScore() + PAYBACK_SCORE,
            answer.getMember().getScore() + question.getReward()) //변경 후 스코어(질문자,답변자 중 큰쪽)
    );

    //채택된 답변에 채택 시간(picked_at) 세팅
    answer.setPickedAt(LocalDateTime.now());
  }

  public void setKeywords(Question question, QuestionForm form) {

    List<String> keywords = KeywordUtil.getKeywords(form);
    Map<String, List<String>> keywordMap = new HashMap<>();
    keywordMap.put("keywords", keywords);
    question.setKeywords(keywordMap);
    questionRepository.save(question);

    for (String keyword : keywords) {
      rankService.updateKeywordRank(keyword);
    }
  }
}
