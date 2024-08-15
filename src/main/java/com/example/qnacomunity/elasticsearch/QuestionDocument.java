package com.example.qnacomunity.elasticsearch;

import com.example.qnacomunity.entity.Question;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@Builder
@Setting(settingPath = "/elasticsearch/settings/settings.json", replicas = 0)
@Mapping(mappingPath = "/elasticsearch/mappings/mappings.json")
@Document(indexName = "questions")
public class QuestionDocument {

  @Id
  private Long id;

  private String title;

  private String content;

  private String keywords;

  private long created;

  public static QuestionDocument from(Question question) {

    return QuestionDocument.builder()
        .id(question.getId())
        .title(question.getTitle())
        .content(question.getContent())
        .keywords(String.join(" ", question.getKeywords().get("keywords")))
        .created(Timestamp.valueOf(question.getCreatedAt()).getTime())
        .build();
  }
}
