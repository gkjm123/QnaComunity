package com.example.qnacomunity.service;

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
import com.example.qnacomunity.repository.MemberRepository;
import com.example.qnacomunity.repository.QuestionRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaService {

  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public QuestionResponse createQuestion(MemberResponse memberResponse, QuestionForm form) {

    Member member = getMember(memberResponse);

    Question question = Question.builder()
        .member(member)
        .title(form.getTitle())
        .content(form.getContent())
        .reward(form.getReward())
        .build();

    return QuestionResponse.from(questionRepository.save(question));
  }

  @Transactional
  public QuestionResponse getQuestion(Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    //질문 조회수 1 증가
    question.setHits(question.getHits() + 1);

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

  @Transactional
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

    //질문에 답변이 달려있는지
    boolean isQuestionAnswered = answerRepository.existsByQuestion_Id(questionId);

    //질문 달린 답변은 수정 불가
    if (isQuestionAnswered) {
      throw new CustomException(ErrorCode.Q_ANSWERED);
    }

    question.setTitle(form.getTitle());
    question.setContent(form.getContent());
    question.setReward(form.getReward());

    return QuestionResponse.from(questionRepository.save(question));
  }

  @Transactional
  public void deleteQuestion(MemberResponse memberResponse, Long questionId) {

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.Q_NOT_FOUND));

    //질문자 본인 맞는지 체크
    if (!Objects.equals(question.getMember().getId(), memberResponse.getId())) {
      throw new CustomException(ErrorCode.Q_MEMBER_NOT_MATCH);
    }

    //질문에 답변이 달려있는지
    boolean isQuestionAnswered = answerRepository.existsByQuestion_Id(questionId);

    //질문 달린 답변은 삭제 불가
    if (isQuestionAnswered) {
      throw new CustomException(ErrorCode.Q_ANSWERED);
    }

    questionRepository.deleteById(questionId);
  }

  @Transactional
  public AnswerResponse createAnswer(MemberResponse memberResponse, AnswerForm form) {

    Member member = getMember(memberResponse);

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

  @Transactional
  public void pickAnswer(MemberResponse memberResponse, Long answerId) {

    Member member = getMember(memberResponse);

    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new CustomException(ErrorCode.A_NOT_FOUND));

    Question question = answer.getQuestion();

    //질문자 본인 체크
    if (!Objects.equals(question.getMember().getId(), member.getId())) {
      throw new CustomException(ErrorCode.Q_MEMBER_NOT_MATCH);
    }

    //질문에 채택된 답변이 있는지
    boolean isQuestionPicked = answerRepository
        .existsByQuestion_IdAndPickedAtIsNotNull(question.getId());

    //채택된 질문 있으면 다시 채택 불가
    if (isQuestionPicked) {
      throw new CustomException(ErrorCode.Q_PICKED);
    }

    //채택된 답변에 채택 시간(picked_at) 세팅
    answer.setPickedAt(LocalDateTime.now());
  }

  public Member getMember(MemberResponse memberResponse) {

    return memberRepository.findById(memberResponse.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
