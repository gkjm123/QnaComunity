package com.example.qnacomunity.dto.response;

import com.example.qnacomunity.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

  private Long id;
  private String nickName;
  private String email;
  private String role;
  private int score;
  private String profileUrl;
  private LocalDateTime createdAt;
  private LocalDateTime deletedAt;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .nickName(member.getNickName())
        .email(member.getEmail())
        .role(member.getRole())
        .score(member.getScore())
        .profileUrl(member.getProfileUrl())
        .createdAt(member.getCreatedAt())
        .deletedAt(member.getDeletedAt())
        .build();
  }

}
